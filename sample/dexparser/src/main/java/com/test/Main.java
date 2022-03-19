package com.test;

import com.github.dex.DexData;

import java.io.File;
import java.io.RandomAccessFile;

public class Main {
    public static void main(String[] args) {
        try {
            DexData dexData = new DexData(
                    new RandomAccessFile(new File("/Users/zhouyanxia/Downloads/z2022030101_xlcw_webpay_zt_30.1.41_202203071545_l1099/classes.dex"), "r"));
            dexData.load();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
