#导入我们需要用到的包和类并且起别名
import sys,time,datetime
from com.android.monkeyrunner import MonkeyRunner as mr
from com.android.monkeyrunner import MonkeyDevice as md
from com.android.monkeyrunner import MonkeyImage as mi
deviceslist = []
devices = []
snapshot = []
templist = []
f = open("monkeyrunner/takeSnapshot/devices.txt")
while True:
	line = f.readline()
	if line:
		templist.append(line.strip())
	else:
		break;
f.close()
templist.pop()
for i in range(len(templist)):
	deviceslist.append(templist[i].split('\t'))
fc = open("monkeyrunner/takeSnapshot/componentName.txt")
complist = []
while True:
	comp = fc.readline()
	if comp:
		complist.append(comp.strip())
	else:
		break;
fc.close()
fp = open("monkeyrunner/takeSnapshot/apk.txt")
apklist = []
while True:
	apk = fp.readline()
	if apk:
		apklist.append(apk.strip())
	else:
		break;
print 'apk list :'
print apklist
print 'start componentName list :'
print complist
print 'devices list:'
print deviceslist
for i in range(1,len(deviceslist)):
	print 'current devices:'
	print deviceslist[i]
	devices.append(mr.waitForConnection(1.0,deviceslist[i][0]))
	#安装apk文件
	for j in range(len(apklist)):
		devices[i-1].installPackage('monkeyrunner/takeSnapshot/apk/'+apklist[j])
	#启动activity
	for k in range(len(complist)):
		print 'current start activity:'
		print complist[k]
		devices[i-1].startActivity(component=complist[k])
		#设置延时秒数
		mr.sleep(10.0)
		#----------------
		#这里可进行一定的UI操作
		#----------------
		#mr.sleep(3.0)
		#进行截图
		snapshot.append(devices[i-1].takeSnapshot())
		print 'end snapshot'
		#创建时间字符串
		t = time.strftime("%Y-%m-%d-%X",time.localtime())
		t = t.replace(":","-")
		#保存截图
		package = complist[k].replace('/.','.')
		snapshot[0].writeToFile('monkeyrunner/takeSnapshot/'+deviceslist[i][0]+'-'+t+'-'+package+'.png','png');
		snapshot.pop()
