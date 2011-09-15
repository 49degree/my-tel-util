package com.guanri.android.jpos.services; 
interface GrPosService { 
        boolean startPos(); 
        boolean stopPos();
        String  operate(String params);
}