#!/usr/bin/python

from subprocess import Popen, PIPE
from threading import Thread
import commons, getopt, os, subprocess, sys

PWD = os.getcwd()
DEPLOYMENT = 'deployment'
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
        elif commons.strip_dashes(opt) in commons.deploy_vars:
            print('\tDetected ' + commons.strip_dashes(opt))
            commons.deploy_vars[commons.strip_dashes(opt)] = True
        else:
            # This situation shouldn't occur because the valid options
            # are determined by the deploy_vars dictionary.
            print(opt + ' wasn\'t recognized as a valid command.')

    commons.set_window_title('Schema Wizard Deployment')
    commons.ENVIRONMENT = DEPLOYMENT
    commons.dir_setup()
    
    if commons.deploy_vars['webapp-only'] is True:
        commons.kill_jetty()
        commons.start_jetty()
    elif commons.deploy_vars['backend-only'] is True:
        commons.kill_backend()
        commons.start_backend()
    else: 
        commons.kill_all_children()
        _run_deployment()
 
""" Deployment methods """
def _run_deployment():    
    # H2 deployment
    if (commons.deploy_vars['delete-h2-cache'] == True):
        commons.delete_h2_cache()
    commons.start_h2()
    
    # MongoDB deployment
    commons.start_mongo_db()
    if commons.deploy_vars['delete-mongo-cache'] is True:
        commons.delete_mongo_cache
    if commons.deploy_vars['reset-mongo-to-default'] is True:
        commons.reset_mongo_to_state(commons.SCRIPT_CONTEXT + '\\..\\interpretation-engine-mongodb')
    if commons.deploy_vars['use-industry-pack'] is True:
        commons.reset_mongo_to_state(commons.SCRIPT_CONTEXT + '\\..\\..\\digitaledge_datamodeltoolkit_industrypacks\\industry-domains')
    
    commons.start_python()
    
    if commons.deploy_vars['delete-jetty-cache'] is True:
        commons.delete_jetty_cache()
    commons.start_jetty()
    
def _allowed_options():
     # Pre-define the help method
        long_opts_allowed = ['help']
        
        # Populate the rest of the valid options with the values
        # of the deploy_vars dictionary.
        for key in commons.deploy_vars.keys():
            long_opts_allowed.append(key)
        
        return long_opts_allowed
    
def _print_usage():
    print('\nUsage:')
    print('This is the deploy script for the Schema Wizard application.')
    print('The default command will deploy all dependent services in the')
    print('same state that they were in before the services were stopped.')
    print('Use the following arguments to modify the deployment.')
    print('\npython ' + __file__)
    
    for option in _allowed_options():
        print('\t--' + option)
    
""" Main watcher """
if __name__ == "__main__":
    main(sys.argv[1:])
