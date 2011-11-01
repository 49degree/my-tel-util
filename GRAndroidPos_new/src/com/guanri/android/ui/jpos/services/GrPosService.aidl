package com.guanri.android.ui.jpos.services; 
interface GrPosService { 
        boolean startPos(); 
        boolean stopPos();
        boolean hasCommPort();
        String  operate(String params);
}