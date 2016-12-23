#!/usr/bin/python

from subprocess import check_output, Popen, PIPE, STDOUT
import getopt, os, subprocess, sys, time

build_vars = {
               'run-integration-tests': False,
               'webapp-only': False,
               'and-industry-pack': False,
               'industry-pack-only': False
               }

deploy_vars = {
               'webapp-only': False,
               'backend-only': False,
               'delete-h2-cache': False,
               'delete-mongo-cache': False,
               'delete-jetty-cache': False,
               'reset-mongo-to-default': False,
               'use-industry-pack': False,
               'use-cached-war': False
               }

HELP_KEY = 'h'
SCRIPT_CONTEXT = os.getcwd()
BUILD = 'build'
DEPLOYMENT = 'deployment'
CMD = 'cmd.exe'
ENVIRONMENT = ''
BUILD_PROPERTIES_LOCATION = SCRIPT_CONTEXT + '\\' + BUILD + '\\build.properties'
    
""" Deployment methods """
def start_h2():
    print('\nStarting H2')
    H2_SERVER_LOCATION = SCRIPT_CONTEXT + '\\' + ENVIRONMENT + '\\server\\h2'
    
    commands = []
    commands = commands + ['echo off \n']
    commands = commands + ['call bigtext H2 Database\n']
    
    commands = commands + ['cd ' + ENVIRONMENT + '\\server \n']
    commands = commands + ['rmdir /s /q h2 \n']
    commands = commands + ['mkdir h2 \n']
    
    commands = commands + ['cd ' + SCRIPT_CONTEXT + ' \n']
    commands = commands + ['cd .. \n']
    commands = commands + ['cd h2-database\\target \n']
    commands = commands + ['COPY schwiz-h2.jar ' + H2_SERVER_LOCATION + ' \n']
    
    commands = commands + ['cd ' + H2_SERVER_LOCATION + ' \n']
    commands = commands + ['start cmd /k \"title H2'
                               + ' & java -classpath schwiz-h2.jar com.deleidos.hd.h2.H2Database -tcpPort 9123 -tcpAllowOthers\" \n']
    
    minified_command = minify_command(commands)
    run_commands(minified_command, False)
    
    while not os.path.exists(SCRIPT_CONTEXT + '\\' + ENVIRONMENT + '\\server\\h2\\h2.log'):
        time.sleep(1)
    h2log = open(SCRIPT_CONTEXT + '\\' + ENVIRONMENT + '\\server\\h2\\h2.log')
    while 'Server running at tcp://' not in h2log.read():
        continue
    
def start_mongo_db():
    print('\nStarting MongoDB')    
    MONGODB_SERVER_LOCATION = SCRIPT_CONTEXT + '\\' + ENVIRONMENT + '\\server\\mongodb'
    
    commands = []    
    commands = commands + ['echo off \n']
    commands = commands + ['call bigtext Mongo Database\n']
    
    commands = commands + ['cd ' + MONGODB_SERVER_LOCATION + ' \n']
    commands = commands + ['rmdir /s /q logs \n']
    commands = commands + ['mkdir logs \n']
        
    if ENVIRONMENT is DEPLOYMENT:
        commands = commands + ['start cmd /k \"echo MongoDB starting up.. ' 
                               + ' & echo Logs are viewable at: ' + MONGODB_SERVER_LOCATION + 'logs\\mongo.log' 
                               + ' & echo Closing this window will shut down MongoDB.'
                               + ' & title MongoDB'
                               + ' & mongod --dbpath ' + MONGODB_SERVER_LOCATION
                               + ' --logpath ' + MONGODB_SERVER_LOCATION + '\\logs\\mongo.log\"\n']
    elif ENVIRONMENT is BUILD:
        commands = commands + ['start cmd /k \"echo MongoDB starting up.. ' 
                               + ' & echo Logs are viewable at: ' + MONGODB_SERVER_LOCATION + '\\logs\\mongo.log' 
                               + ' & echo Closing this window will shut down MongoDB.'
                               + ' & title MongoDB'
                               + ' & mongod --dbpath ' + MONGODB_SERVER_LOCATION
                               + ' --logpath ' + MONGODB_SERVER_LOCATION + '\\logs\\mongo.log\"\n']
    else:
        print('Environment was not set. MongoDB failed to start.')
        # be sure to call ENVIRONMENT and set it appropriately before calling any 
        # method from the commons file
        
    minified_command = minify_command(commands)
    run_commands(minified_command, False)
    
    while not os.path.exists(SCRIPT_CONTEXT + '\\' + ENVIRONMENT + '\\server\\mongodb\\logs\\mongo.log'):
        time.sleep(1)
    mongolog = open(SCRIPT_CONTEXT + '\\' + ENVIRONMENT + '\\server\\mongodb\\logs\\mongo.log')
    while 'waiting for connections' not in mongolog.read():
        continue
    
def start_python():
    print('\nStarting Interpretation Engine.')    
    PYTHON_SERVER_LOCATION = SCRIPT_CONTEXT + '\\' + ENVIRONMENT + '\\server\\python'
    
    commands = []
    commands = commands + ['echo off \n']
    commands = commands + ['call bigtext Interpretation Engine\n']
    
    commands = commands + ['cd ' + ENVIRONMENT + '\\server \n']
    commands = commands + ['rmdir /s /q python \n']
    commands = commands + ['mkdir python \n']
    
    commands = commands + ['cd ' + SCRIPT_CONTEXT + ' \n']
    commands = commands + ['cd .. \n']
    commands = commands + ['cd interpretation-engine\\src\\main \n']
    commands = commands + ['echo d | xcopy python ' + PYTHON_SERVER_LOCATION + ' /s /e /y \n']
    
    commands = commands + ['cd ' + PYTHON_SERVER_LOCATION + ' \n']
    commands = commands + ['start cmd /k \"title Python'
                               + ' & python start.py\" \n']
    
    minified_command = minify_command(commands)
    run_commands(minified_command, False)
    
    while not os.path.exists(SCRIPT_CONTEXT + '\\' + ENVIRONMENT + '\\server\\python\\interpretation_engine.log'):
        time.sleep(1)
    ielog = open(SCRIPT_CONTEXT + '\\' + ENVIRONMENT + '\\server\\python\\interpretation_engine.log')
    while 'Running on http://' not in ielog.read():
        continue
    
def start_jetty():    
    JETTY_SERVER_LOCATION = SCRIPT_CONTEXT + '\\' + ENVIRONMENT + '\\server\\jetty'
    
    if deploy_vars['use-industry-pack'] is True:
        WAR_LOCATION = SCRIPT_CONTEXT + '\\..\\..\\digitaledge_datamodeltoolkit_industrypacks\\industry-addon\\target'
    else:
        WAR_LOCATION = SCRIPT_CONTEXT + '\\..\\schema-wizard\\target'
    
    commands = []    
    commands = commands + ['echo off \n']
    commands = commands + ['call bigtext Jetty\n']
    
    commands = commands + ['RMDIR /s /q \\uploads \n']
    commands = commands + ['RMDIR /s /q \\logs \n']
    
    if deploy_vars['use-cached-war'] is False:
        commands = commands + ['cd ' + JETTY_SERVER_LOCATION + ' \n']
        commands = commands + ['rmdir /s /q webapps \n']
        commands = commands + ['mkdir webapps \n']
        
        commands = commands + ['cd ' + WAR_LOCATION + ' \n']
        commands = commands + ['COPY schwiz.war ' + JETTY_SERVER_LOCATION + '\\webapps \n']
    
    commands = commands + ['cd ' + JETTY_SERVER_LOCATION + ' \n']
    commands = commands + ['start cmd /k \"title Jetty'
                               + ' & java -Xmx2048m -Xms2048m -Xdebug -agentlib:jdwp=transport=dt_socket,address=9999,server=y,suspend=n -jar "%JETTY_HOME%\\start.jar" STOP.PORT=8888 STOP.KEY=stop\" \n']
    
    minified_command = minify_command(commands)
    run_commands(minified_command, False)
    
def start_backend():
    start_h2()
    start_mongo_db()
    start_python()
    
def set_window_title(title):
    run_commands(minify_command(['title ' + title + '\n']), True)
    
def kill_command_window(title):    
    run_commands(minify_command(['taskkill /t /f /fi \"windowtitle eq ' + title + '*\" \n']), True)
    
def kill_h2():
    kill_command_window('H2')
    
def kill_mongo_db():
    kill_command_window('MongoDB')
    
def kill_python():
    kill_command_window('Python')
    
def kill_jetty():
    kill_command_window('Jetty')
    
def kill_backend():
    kill_h2()
    kill_mongo_db()
    kill_python()

def kill_all_children():
    """ Children command windows """
    kill_h2()
    kill_mongo_db()
    kill_python()
    kill_jetty()
    
""" Pre-configuration methods """
# Delete H2 cache
def delete_h2_cache():
    print('Deleting H2 cache.')
    
    commands = []
    commands = commands + ['echo off \n']
    commands = commands + ['cd %USERPROFILE% \n']
    commands = commands + ['rmdir /s /q h2 \n']
    commands = commands + ['mkdir h2 \n']
    
    minified_command = minify_command(commands)
    run_commands(minified_command, True)

# Delete MongoDB cache
def delete_mongo_cache():
    print('Deleting MongoDB cache.')
    
    commands = []
    commands = commands + ['echo off \n']
    commands = commands + ['mongo geo2 --eval \"db.dropDatabase()\" \n']
    commands = commands + ['mongo domain_manager --eval \"db.dropDatabase()\" \n']
    commands = commands + ['mongo reverse_geo --eval \"db.dropDatabase()\" \n']
    commands = commands + ['mongo reverse-geo --eval \"db.dropDatabase()\" \n']
    
    minified_command = minify_command(commands)
    run_commands(minified_command, True)
    
# Reinitialize MongoDB to a default state
def reset_mongo_to_state(state):
    MONGO_DB_LOCATION = state
    
    delete_mongo_cache()
    
    commands = []
    commands = commands + ['echo off \n']
    
    commands = commands + ['mongoimport --db reverse_geo --collection geospatial --file ' + MONGO_DB_LOCATION + '\\country_polygon_data.json \n']
    commands = commands + ['mongoimport --db domain_manager --collection domains --file ' + MONGO_DB_LOCATION + '\\target\\domain_imports.json \n']
    commands = commands + ['mongoimport --db domain_manager --collection interpretations --file ' + MONGO_DB_LOCATION + '\\target\\interpretation_imports.json \n']
    
    commands = commands + ['mongo reverse_geo --eval \"db.geospatial.createIndex({geometry: \'2dsphere\'})\" \n']
    
    minified_command = minify_command(commands)
    run_commands(minified_command, True)
    
def delete_jetty_cache():
    print('Deleting Jetty cache.')
    
    commands = []
    commands = commands + ['echo off \n']
    commands = commands + ['cd %TMP% \n']
    commands = commands + [':: This operation may take a while depending on how many files exist.']
    commands = commands + ['FOR /D %a in (jetty-*) DO RMDIR /S /Q %a \n']
    
    minified_command = minify_command(commands)
    run_commands(minified_command, True)
    
""" Utility methods """        
def strip_dashes(opt):
    if (opt[:2] == '--'):
        return opt.replace('-', '', 2)
    else:
        return opt.replace('-', '', 1)
    
def dir_setup():
    print('\nBuilding the directory structure for: ' + ENVIRONMENT)
    # Build dir structure
    commands = []
    commands = commands + ['echo off \n']
    commands = commands + ['mkdir ' + ENVIRONMENT + ' & cd ' + ENVIRONMENT + ' \n']
    commands = commands + ['mkdir server & cd server \n']
    commands = commands + ['mkdir h2 \n']
    commands = commands + ['mkdir python \n']
    commands = commands + ['mkdir mongodb\logs \n']
    if ENVIRONMENT is 'deployment': 
        commands = commands + ['mkdir jetty & cd jetty \n']
        commands = commands + ['mkdir webapps \n']

    minified_command = minify_command(commands)
    run_commands(minified_command, True)
    
def print_build_count():
    commands = []
    commands = commands + ['echo off \n']
    commands = commands + ['if not exist buildcount.txt echo 1 > buildcount.txt \n']
    commands = commands + ['for /f %i in (buildcount.txt) do set /a %i + 1 > buildcount.txt \n']
    commands = commands + ['for /f %i in (buildcount.txt) do call bigtext Build Count: %i \n']
    commands = commands + ['ping -n 2 127.0.0.1 >nul \n']

    minified_command = minify_command(commands)
    run_commands(minified_command, True)
    
def clear_screen():
    commands = []
    commands = commands + ['cls \n']

    minified_command = minify_command(commands)
    run_commands(minified_command, True)
    
def minify_command(commands):
    separator = ' '
    return separator.join(str(command) for command in commands)

def run_command_with_streaming_output(command, path):
    """ This is used when real-time output is necessary.
    Output prints to the parent command window """
    proc = subprocess.Popen(command, stdin=None, stdout=None, shell=True, cwd=path)
    proc.wait()
    
def run_commands(minified_command, wait):
    """ One advantage of this method over the streaming output method 
    is that 'cd's are allowed, whereas they are ignored in the streaming
    output method """
    proc = subprocess.Popen(CMD, stdin=subprocess.PIPE, stdout=None)
    proc.stdin.flush()
    stdout, stderr = proc.communicate(str.encode(minified_command))
    if wait is True:
        proc.wait()