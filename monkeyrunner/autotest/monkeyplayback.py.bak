#!/usr/bin/env monkeyrunner
# Copyright 2010, The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

import sys,time,datetime,os

from com.android.monkeyrunner import MonkeyImage as mi
from com.android.monkeyrunner import MonkeyRunner
from com.android.monkeyrunner.easy import EasyMonkeyDevice
from com.android.monkeyrunner.easy import By
from  org.python.core import PyTuple

# The format of the file we are parsing is very carfeully constructed.
# Each line corresponds to a single command.  The line is split into 2
# parts with a | character.  Text to the left of the pipe denotes
# which command to run.  The text to the right of the pipe is a python
# dictionary (it can be evaled into existence) that specifies the
# arguments for the command.  In most cases, this directly maps to the
# keyword argument dictionary that could be passed to the underlying
# command. 

# Lookup table to map command strings to functions that implement that
# command.
CMD_MAP = {
    'TOUCH': lambda dev, arg: dev.touch(**arg),
    'DRAG': lambda dev, arg: dev.drag(**arg),
    'PRESS': lambda dev, arg: dev.press(**arg),
    'TYPE': lambda dev, arg: dev.type(**arg),
    'WAIT': lambda dev, arg: MonkeyRunner.sleep(**arg),
    'START_ACTIVITY':lambda dev, arg:dev.startActivity(**arg),
    'RECORD_IMAGE':lambda dev, arg:dev.takeSnapshot().writeToFile(**arg),
    }
#now time string
nowtimeStr = time.strftime('%Y-%m-%d-%H-%M-%S',time.localtime(time.time()))
#compare the image index x,y,width,height
cutImageArgs = [0,0,0,0]
# Process a single file for the specified device.
def process_file(fp, device):
    workpath = os.getcwd()+'\\monkeyrunner\\record\\'
    lineStr = fp.readline().rstrip()
    items = lineStr.split('|')
    if items[0] == 'workpath':
	workpath = os.getcwd()+items[1]
    print workpath
    if os.path.exists(workpath):
	os.makedirs(workpath+nowtimeStr)
    else:
	os.makedirs(workpath)
    #record the error log
    errorLogFileHandle = open(workpath+'\errorLog.txt', 'a' )  
    #record the succ log
    succLogFileHandle = open(workpath+'\succLog.txt', 'a' )

    for line in fp:
	line = line.rstrip()
        if line == '':
		continue
	print line
        try:
            # Parse the pydict
	    items = line.split('|')
	    if items[0] == '#':
		continue
	    cmd = items[0]
	    rest = items[1]
            rest = eval(rest)
        except:
            print 'unable to parse options'
	    continue
        if cmd not in CMD_MAP:
		if cmd == 'cutImageArgs':
			cutImageArgs = rest;
		continue
	# if cmd is RECORD_IMAGE compare the image
	if cmd == 'RECORD_IMAGE':
	    print 'is RECORD_IMAGE cmd'
	    #ddata=json.loads(rest)
	    oldImagePath = workpath+'\\'+rest['path']
	    #check is have the image
	    if os.path.isfile(oldImagePath):
	        rest['path'] = workpath+'\\'+nowtimeStr+'\\'+rest['path']
	        #rest = json.dumps(ddata)
	        CMD_MAP[cmd](device, rest)
		#compare the image
		tempCutImageArgs = cutImageArgs
		if len(items) >= 3:
		   tempCutImageArgs = eval(items[2]) 
		print tempCutImageArgs
		newImage = MonkeyRunner.loadImageFromFile(rest['path']).getSubImage(PyTuple(tempCutImageArgs))
		newImage.writeToFile(rest['path']+'new.png');
		oldImage = MonkeyRunner.loadImageFromFile(oldImagePath).getSubImage(PyTuple(tempCutImageArgs))
		newImage.writeToFile(rest['path']+'old.png');
		if newImage.sameAs(oldImage):
			print 'image is same' 
			succLogFileHandle.write (oldImagePath + ' is same\n') 
		else:
			print 'image is not same'
			errorLogFileHandle.write (oldImagePath + ' is not same\n') 
	    else:
	         rest['path'] = oldImagePath
	         #rest = json.dumps(ddata)
		 CMD_MAP[cmd](device, rest)
	else:
		CMD_MAP[cmd](device, rest) 

	MonkeyRunner.sleep(1)
    errorLogFileHandle.close() 
    succLogFileHandle.close() 
def main():
	device = MonkeyRunner.waitForConnection()
	index = 0
	for file in sys.argv:
	    #file = sys.argv[1]
	    print file
	    index = index+1
	    if index < 2:
		continue
	    fp = open(file, 'r')
	    
	    process_file(fp, device)
	    fp.close()
	    print 'fp.close()'
    

if __name__ == '__main__':
    main()



