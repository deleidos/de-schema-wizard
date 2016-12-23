#!/usr/bin/python

from subprocess import Popen, PIPE
from threading import Thread
import commons, getopt, os, subprocess, sys

PWD = os.getcwd()
BUILD = 'build'
CMD = 'cmd.exe'

def main(argv):  
    try:            
        # Get the command opts
        opts, args = getopt.getopt(argv, commons.HELP_KEY, _allowed_options())
    except getopt.GetoptError:
        print('\nError: That is not the proper usage. Use \'python ' + __file__ + ' --help\' for more information.\n')
        sys.exit(2)
        
    for opt, arg in opts:
        if commons.strip_dashes(opt) == 'h' or commons.strip_dashes(opt) == 'help':
            _print_usage()
            sys.exit()
        elif commons.strip_dashes(opt) in commons.build_vars:
            print('\tDetected ' + commons.strip_dashes(opt))
            commons.build_vars[commons.strip_dashes(opt)] = True
        else:
            # This situation shouldn't occur because the valid options
            # are determined by the build_vars dictionary.
            print(opt + ' wasn\'t recognized as a valid command.')
            sys.exit()
            
    commons.set_window_title('Schema Wizard Build')
    commons.ENVIRONMENT = BUILD
    commons.clear_screen()
    commons.print_build_count()
    commons.dir_setup()
    
    print('Setting SW_CONFIG_PROPERTIES to the default value ' + commons.BUILD_PROPERTIES_LOCATION)
    os.environ['SW_CONFIG_PROPERTIES'] = commons.BUILD_PROPERTIES_LOCATION
    
    if (commons.build_vars['webapp-only'] == True):
        _run_build(optional='schema-wizard')
    elif (commons.build_vars['industry-pack-only'] == True):
        _run_build(optional='..\\digitaledge_datamodeltoolkit_industrypacks')
    elif (commons.build_vars['run-integration-tests'] == True):
        commons.kill_all_children()
        _run_build()
    else:
        _run_build() 
        
    if commons.build_vars['and-industry-pack'] is True:
        _run_build(optional='..\\digitaledge_datamodeltoolkit_industrypacks')

""" Build methods """
def _run_build(**kwargs):
    """ kwargs takes the directory name of the project you want to build in 
    the root of the repository. (e.g. digitaledge_datamodeltoolkit/schema-wizard 
    would necessitate the optional argument 'schema-wizard' """
    path = PWD + '\\..'
    if ('optional' in kwargs):
        path = PWD + '\\..\\' + kwargs['optional']
        print('Clearing the parameter for integration-tests because '
        + 'integration tests can only run on full builds.')
        commons.build_vars['run-integration-tests'] = False
        
    print('\nBuilding the Schema Wizard project.')
    build_command = 'mvn clean install \n'
    
    if (commons.build_vars['run-integration-tests']):
        # Start H2, MongoDB, and Python
        build_command = 'call mvn clean install -Pintegration-tests-windows \n'
        
        commons.delete_h2_cache()
        commons.start_h2()
    
        commons.start_mongo_db()
        commons.reset_mongo_to_state(commons.SCRIPT_CONTEXT + '\\..\\..\\digitaledge_datamodeltoolkit_industrypacks\\industry-domains')
        
        commons.start_python()
    
    commands = []
    commands = commands + [build_command]
    
    for command in commands:
        commons.run_command_with_streaming_output(command, path)
        
def _allowed_options():
     # Pre-define the help method
        long_opts_allowed = ['help']
        
        # Populate the rest of the valid options with the values
        # of the build_vars dictionary.
        for key in commons.build_vars.keys():
            long_opts_allowed.append(key)
        
        return long_opts_allowed
    
def _print_usage():
    print('\nUsage:')
    print('This is the build script for the Schema Wizard application.')
    print('\npython ' + __file__)
    
    for option in _allowed_options():
        print('\t--' + option)
    
""" Main watcher """
if __name__ == "__main__":
    main(sys.argv[1:])
