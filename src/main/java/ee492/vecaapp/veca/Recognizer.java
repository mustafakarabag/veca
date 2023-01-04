package ee492.vecaapp.veca;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;


import org.jtransforms.dct.DoubleDCT_1D;
import org.jtransforms.fft.DoubleFFT_1D;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import com.jmatio.common.MatDataTypes;
import com.jmatio.io.MatFileReader;

import com.jmatio.types.*;
import com.jmatio.io.*;
/**
 * Created by mustafa on 5.5.2017.
 */

public class Recognizer {
    //load W
    private Context appContext;
    private int frameSize = 25*16000/1000;
    private double[] h = new double[frameSize];
    private double[][] filters;
    private ArrayList<double[][]> W;
    private int numOfFrames;
    String outDir;
    public Recognizer(Context appContext) throws IOException {
        this.appContext = appContext;
        outDir = Environment.getExternalStorageDirectory() + "/veca";
        copyAssets();
        for (int i=0; i<h.length; i++){
            h[i] = 0.54 - 0.46*Math.cos(2*Math.PI*i/(h.length-1));
        }


        File WFile = new File(outDir, "W.mat");
        File filtersFile = new File(outDir, "filters.mat");

        MatFileReader mfrW = new MatFileReader(WFile);
        Map<String,MLArray> content=mfrW.getContent();
        MLCell Wcell =(MLCell) content.get("W");
        W = new ArrayList<>();
        W.add(((MLDouble) Wcell.get(0, 0)).getArray());
        W.add(((MLDouble) Wcell.get(1, 0)).getArray());
        numOfFrames = (W.get(0)[0].length - 1)/13;
        MatFileReader mfrFilters = new MatFileReader(filtersFile);
        content=mfrFilters.getContent();
        MLArray filterscell = content.get("filters");
        filters = ((MLDouble) filterscell).getArray();

    }


    public double[] computeMFCC(double[] frame, double[][] filters){
        double[] mfcc = new double[13];
        int numOfFilters = filters.length;
        double[] hammframe = ewMult(frame,h);

        DoubleFFT_1D fftDo = new DoubleFFT_1D(512);
        double[] fft = new double[512 * 2]; // 512 point fft
        System.arraycopy(hammframe, 0, fft, 0, hammframe.length);
        fftDo.realForwardFull(fft);
        double[] spectrum = new double[256+1];

        spectrum[0] = Math.pow(fft[0],2)/257;
        for (int k=1; k<257 ; k++)
        {
            spectrum[k] = (Math.pow(fft[2*k],2) + Math.pow(fft[2*k +1],2))/257;
        }

        double[] preMFCC = new double[filters.length];
        for (int i=0; i<filters.length;i++){
            preMFCC[i] = Math.log(sumArr(ewMult(spectrum, filters[i])));
        }

        DoubleDCT_1D dctDo = new DoubleDCT_1D(preMFCC.length);
        double[] dct = new double[preMFCC.length]; // 512 point fft
        System.arraycopy(preMFCC, 0, dct, 0, preMFCC.length);
        dctDo.forward(dct, true);

        System.arraycopy(dct, 0, mfcc, 0, 13);


        return mfcc;

    }

    public double sumArr(double[] arr){
        double sum = 0;
        for (int i = 0; i<arr.length;i++){
            sum += arr[i];
        }
        return sum;
    }
    public double[] ewMult(double[] arr1, double[] arr2){
        double[] multarr = new double[arr1.length];
        for (int i=0; i<arr1.length; i++){
            multarr[i] = arr1[i]*arr2[i];
        }
        return multarr;
    }

    public double[][] matMult(double[][] mat1, double[][] mat2){
        int l = mat1.length;
        int m = mat1[0].length;
        int n = mat2[0].length;

        double[][] res = new double[l][n];
        for (int i=0; i<l;i++){
            for (int j=0; j<n;j++){
                double temp = 0;
                for (int k=0; k<m; k++){
                    temp += mat1[i][k] * mat2[k][j];
                }
                res[i][j] = temp;
            }
        }
        return res;
    }


    /*
    public static double ANNff(double[] inpold, ArrayList<double[][]> W){
        double[] inp = new double[inpold.length + 1];
        inp[0] = 1;
        System.arraycopy(inpold,0,inp,1,inpold.length);

        double[][] w1 = W.get(0);
        double[] s1 = new double[w1.length];

        for (int i=0; i<s1.length; i++){
            double temp = 0;
            for (int j=0; j<w1[0].length; j++){
                temp += w1[i][j]*inp[j];
            }
            s1[i] = temp;
        }

        double[] z1 = new double[s1.length];
        for (int i=0; i<s1.length;i++){
            z1[i] = 1.0/(1.0 + Math.exp(-s1[i]));
        }

        //second stage
        double[] inp2 = new double[z1.length + 1];
        inp2[0] = 1;
        System.arraycopy(z1,0,inp2,1,z1.length);


        double[][] w2 = W.get(1);
        double[] s2 = new double[w2.length];

        for (int i=0; i<s2.length; i++){
            double temp = 0;
            for (int j=0; j<w2[0].length; j++){
                temp += w2[i][j]*inp2[j];
            }
            s2[i] = temp;
        }

        double[] z2 = new double[s2.length];
        for (int i=0; i<s2.length;i++){
            z2[i] = 1.0/(1.0 + Math.exp(-s2[i]));
        }

        return z2[0];
    }
    */

    public double ANNff(double[] inpold, ArrayList<double[][]> W){
        double[] inp = new double[inpold.length + 1];
        inpold = normalizeData(inpold);
        inp[0] = 1;
        System.arraycopy(inpold,0,inp,1,inpold.length);

        double[][] inpMat = new double[inp.length][1];
        for(int i=0; i<inp.length; i++){
            inpMat[i][0] = inp[i];
        }

        double[][] w1 = W.get(0);
        double[][] s1 = new double[w1.length][1];

        s1 = matMult(w1,inpMat);

        double[] z1 = new double[s1.length];
        for (int i=0; i<s1.length;i++){
            z1[i] = 1.0/(1.0 + Math.exp(-s1[i][0]));
        }

        //second stage
        double[] inp2 = new double[z1.length + 1];
        inp2[0] = 1;
        System.arraycopy(z1,0,inp2,1,z1.length);

        double[][] inp2Mat = new double[inp2.length][1];
        for(int i=0; i<inp2.length; i++){
            inp2Mat[i][0] = inp2[i];
        }


        double[][] w2 = W.get(1);
        double[][] s2 = matMult(w2,inp2Mat);


        double[] z2 = new double[s2.length];
        for (int i=0; i<s2.length;i++){
            z2[i] = 1.0/(1.0 + Math.exp(-s2[i][0]));
        }

        return z2[0];
    }

    private double[] normalizeData(double[] inp) {
        double tot = 0;
        for (int i=0;i<inp.length;i++){
            tot += inp[i];
        }
        double mean = tot/inp.length;
        double[] meanlessInp= new double[inp.length];
        double vartot = 0;
        for (int i=0;i<inp.length;i++){
            meanlessInp[i] = inp[i]-mean;
            vartot += meanlessInp[i]*meanlessInp[i];
        }
        double std = Math.sqrt(vartot/inp.length);
        double[] normInp = new double[inp.length];
        for (int i=0;i<inp.length;i++){
            normInp[i] = meanlessInp[i]/std;
        }
        return normInp;
    }

    private void copyAssets(){
        AssetManager assetManager = appContext.getAssets();
        String[] files = null;
        try {
            files = assetManager.list("");
        } catch (IOException e) {
            e.printStackTrace();
        }

        for(String filename : files) {
            InputStream in = null;
            OutputStream out = null;
            try {
                in = assetManager.open(filename);

                File outFile = new File(outDir, filename);
                out = new FileOutputStream(outFile);
                copyFile(in, out);
                in.close();
                in = null;
                out.flush();
                out.close();
                out = null;
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void copyFile(InputStream in, OutputStream out) {
        byte[] buffer = new byte[1024];
        int read;
        try {
            while((read = in.read(buffer)) != -1){
                out.write(buffer, 0, read);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public double decide(double[] utt) {
            if (utt.length<4000 || utt.length>15000){
                return 0;
            }
            double[] mfcc = getMFCCArray(utt);
            double result = ANNff(mfcc,W);
            return result;
    }

    private double[] getMFCCArray(double[] utt) {
        int stepSize = (int) Math.floor((utt.length - frameSize)/(numOfFrames-1));
        double max = 0;
        for (int i=0; i<utt.length;i++){
            if (Math.abs(utt[i]) > max){
                max = Math.abs(utt[i]);
            }
        }
        for (int i=0; i<utt.length;i++){
            utt[i] = utt[i]/max;
        }

        double[] mfccArray = new double[numOfFrames*13];
        for (int i=0; i<numOfFrames; i++){
            double[] frame = Arrays.copyOfRange(utt, i*stepSize, frameSize + i*stepSize);
            double[] frameCoeff = computeMFCC(frame,filters);
            for (int j=0;j<13;j++){
                mfccArray[i*13+j] = frameCoeff[j];
            }
        }

        return mfccArray;

    }
}
