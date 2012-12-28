#!/usr/bin/envmonkeyrunner
# copyright 2010, theandroid open source project
#
# licensed under theapache license, version 2.0 (the "license");
# you may not usethis file except in compliance with the license.
# you may obtain acopy of the license at
#
#     http://www.apache.org/licenses/license-2.0
#
# unless required byapplicable law or agreed to in writing, software
# distributed underthe license is distributed on an "as is" basis,
# without warrantiesor conditions of any kind, either express or implied.
# see the license forthe specific language governing permissions and
# limitations underthe license.
 
from com.android.monkeyrunner import MonkeyRunner as mr
from com.android.monkeyrunner.recorder import MonkeyRecorder as recorder
 
device =mr.waitForConnection()
recorder.start(device)