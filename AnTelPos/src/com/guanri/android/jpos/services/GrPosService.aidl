package com.guanri.android.jpos.services; 
interface GrPosService { 
        boolean startPos(); 
        boolean stopPos();
        boolean hasCommPort();
        String  operate(String params);
}