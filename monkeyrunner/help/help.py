from com.android.monkeyrunner import MonkeyRunner, MonkeyDevice

content = MonkeyRunner.help("html");
f = open('.\monkeyrunner\help\help.html', 'w');
f.write(content);
f.close();