package com.guanri.fsk.utils.fourier;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * 参见电子计算机算法手册，Page 115
 * 快速傅里叶变换算法(2)
 * 
 */
public class Fourier {

    static public double log(double value, double base) {
        return Math.log(value) / Math.log(base);
    }
    //反序运算
    private static int bitRev(int j, int m) {
        int i, j1 = j, k = 0, j2 = j;
        for (i = 0; i < m; i++) {
            j1 >>= 1;
            k <<= 1;
            k += (j1 - j2);
            j1 = j2;
        }
        return k;
    }
    //反序运算对应的Sin,Cos表
    private static void calcuW(int m, float[] cos, float[] sin) {
        float pi2 = 6.2831853f;
        int m1 = m - 1, n = 1 << m;
        cos = new float[n];
        sin = new float[n];
        for (int k = 0; k < n; k++) {
            int p = bitRev(k / (2 << m1), m);
            float arg = pi2 * p / n;
            cos[k] = (float) Math.cos(arg);
            sin[k] = (float) Math.sin(arg);
        }
    }
    //快速傅里叶变换算法
    //输入：
    //    IsForword =true正变换；=false反变换
    //    a 实部
    //    b 虚部
    //输出：
    //    a 实部
    //    b 虚部
    public static void FFT(boolean IsForword, float[] a, float[] b) {
        if ((a == null) || (b == null)) {
            return; //throw Exception("变换数组不能为空。");
        }
        float[] cos=null, sin=null;
        int i, k = 0, l, n = a.length, n2 = n >> 1, m = (int) log(n, 2);
        if (n != (int) Math.pow(2, m)) {
            return;//throw new Exception("变换数组应该满足2的整数幂关系。");
        }
        float re, im;
        calcuW(m, cos, sin);
        for (l = 0; l < m; l++) {
            while (k < n) {
                for (i = 0; i < n2; i++) {
                    int ii = k + n2;
                    re = a[ii] * cos[k] + b[ii] * sin[k];
                    im = b[ii] * cos[k] - a[ii] * sin[k];
                    a[ii] = a[k] - re;
                    b[ii] = b[k] - im;
                    a[k] += re;
                    b[k] += im;
                    k++;
                }
                k += n2;
            }
            k = 0;
            n2 >>= 1;
        }
        for (k = 0; k < n; k++) {
            i = bitRev(k, m);
            if (i > k) {
                re = a[k];
                a[k] = a[i];
                a[i] = re;
                im = b[k];
                b[k] = b[i];
                b[i] = im;
            }
            if (!IsForword) {//反变换时使用
                a[k] /= n;
                b[k] /= n;
            }
        }
    }
    //实数快速傅里叶变换算法
    public static void RFFT(boolean IsForward, float[] x) {
        int n = x.length / 2, i;
        float[] a = new float[n], b = new float[n];
        for (i = 0; i < n; i++) {
            a[i] = x[2 * i];
            b[i] = x[2 * i + 1];
        }
        if (IsForward) {
            FFT(IsForward, a, b);
        }
        float sd = (float) Math.sin(Math.PI / n), cd = (float) Math.cos(Math.PI / n), cn = 1.0f, sn = 1.0f;
        for (int j = 0; j < n / 2; j++) {
            int k = n - j;
            if (k == n) {
                k = 0;
            }
            float aa = (a[j] + a[k]) / 2,
                    ab = (a[j] - a[k]) / 2,
                    ba = (b[j] + b[k]) / 2,
                    bb = (b[j] - b[k]) / 2,
                    re = aa + cn * ba,
                    im = bb - cn * ab,
                    r = sn * ab;
            a[j] = re - r;
            a[k] = re + r;

            r = -sn * ba;
            b[j] = r + im;
            b[k] = r - im;

            r = cd * cn - sn * sd;
            sn = cn * sd + sn * cd;
            cn = r;
        }
        if (!IsForward) {
            FFT(IsForward, a, b);
        }
        for (i = 0; i < n; i++) {
            x[2 * i] = a[i];
            x[2 * i + 1] = b[i];
        }
    }
    //获取矩阵mat中行(isRow)或列向量
    private static float[] getVector(boolean isRow, int idx, float[][] mat) {
        int numV = (isRow) ? mat.length : mat[0].length;
        float[] vct = new float[numV];
        for (int i = 0; i < numV; i++) {
            vct[i] = (isRow) ? mat[i][idx] : mat[idx][i];
        }
        return vct;
    }
    //设置矩阵mat中行(isRow)或列向量
    private static void setVector(boolean isRow, int idx, float[] vct, float[][] mat) {
        int numV = (isRow) ? mat.length : mat[0].length;
        for (int i = 0; i < numV; i++) {
            if (isRow) {
                mat[i][idx] = vct[i];
            } else {
                mat[idx][i] = vct[i];
            }
        }
    }
    //二维实数快速傅里叶变换算法
    public static void RFFT2(boolean IsForward, float[][] mat) {
        float[] vct1, vct2;
        for (int c = 0; c < mat[0].length; c += 2) {
            vct1 = getVector(true, c, mat);
            vct2 = getVector(true, c + 1, mat);
            FFT(IsForward, vct1, vct2);
            setVector(true, c, vct1, mat);
            setVector(true, c + 1, vct2, mat);
        }
        for (int r = 0; r < mat.length; r += 2) {
            vct1 = getVector(false, r, mat);
            vct2 = getVector(false, r + 1, mat);
            FFT(IsForward, vct1, vct2);
            setVector(false, r, vct1, mat);
            setVector(false, r + 1, vct2, mat);
        }
    }

    public static void main(String[] agrs) {
        float[] a = {2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2}, b = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        FFT(true, a, b);
        FFT(false, a, b);
    }
}


