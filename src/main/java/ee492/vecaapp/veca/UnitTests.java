package ee492.vecaapp.veca;

import java.util.Calendar;

/**
 * Created by mustafa on 31.12.2016.
 */

public class UnitTests {

    public void performTimeTests(SpeechAnalyst speechAnalyst){
        Calendar timeCal;
        String expected;
        String result;
        String input;
        //Case 1
        input = "Bu akşam saat sekize toplantı ayarla";
        timeCal = speechAnalyst.getTime(input);
        expected = "Expected: 20:00";
        if (timeCal == null){
            result = "Result: No Result";
        }
        else {
            result = String.format("%02d", timeCal.get(Calendar.HOUR_OF_DAY)) + ":"+ String.format("%02d", timeCal.get(Calendar.MINUTE));
        }
        System.out.println("Case 1:");
        System.out.println(expected);
        System.out.println(result);
        System.out.println();

        //Case 2
        input = "yedi buçuğa toplantı ayarla";
        timeCal = speechAnalyst.getTime(input);
        expected = "Expected: 19:30";
        if (timeCal == null){
            result = "Result: No Result";
        }
        else {
            result = String.format("%02d", timeCal.get(Calendar.HOUR_OF_DAY)) + ":"+ String.format("%02d", timeCal.get(Calendar.MINUTE));
        }
        System.out.println("Case 2:");
        System.out.println(expected);
        System.out.println(result);
        System.out.println();

        //Case 3
        input = "altıya beş kala";
        timeCal = speechAnalyst.getTime(input);
        expected = "Expected: 17:55";
        if (timeCal == null){
            result = "Result: No Result";
        }
        else {
            result = String.format("%02d", timeCal.get(Calendar.HOUR_OF_DAY)) + ":"+ String.format("%02d", timeCal.get(Calendar.MINUTE));
        }
        System.out.println("Case 3:");
        System.out.println(expected);
        System.out.println(result);
        System.out.println();


        //Case 4
        input = "gece üçte";
        timeCal = speechAnalyst.getTime(input);
        expected = "Expected: 03:00";
        if (timeCal == null){
            result = "Result: No Result";
        }
        else {
            result = String.format("%02d", timeCal.get(Calendar.HOUR_OF_DAY)) + ":"+ String.format("%02d", timeCal.get(Calendar.MINUTE));
        }
        System.out.println("Case 4:");
        System.out.println(expected);
        System.out.println(result);
        System.out.println();

        //Case 5
        input = "üçte";
        timeCal = speechAnalyst.getTime(input);
        expected = "Expected: 15:00";
        if (timeCal == null){
            result = "Result: No Result";
        }
        else {
            result = String.format("%02d", timeCal.get(Calendar.HOUR_OF_DAY)) + ":"+ String.format("%02d", timeCal.get(Calendar.MINUTE));
        }
        System.out.println("Case 5:");
        System.out.println(expected);
        System.out.println(result);
        System.out.println();


        //Case 6
        input = "gece yirmi ikiyi on geçe";
        timeCal = speechAnalyst.getTime(input);
        expected = "Expected: 22:10";
        if (timeCal == null){
            result = "Result: No Result";
        }
        else {
            result = String.format("%02d", timeCal.get(Calendar.HOUR_OF_DAY)) + ":"+ String.format("%02d", timeCal.get(Calendar.MINUTE));
        }
        System.out.println("Case 6:");
        System.out.println(expected);
        System.out.println(result);
        System.out.println();


        //Case 7
        input = "20 30";
        timeCal = speechAnalyst.getTime(input);
        expected = "Expected: 20:30";
        if (timeCal == null){
            result = "Result: No Result";
        }
        else {
            result = String.format("%02d", timeCal.get(Calendar.HOUR_OF_DAY)) + ":"+ String.format("%02d", timeCal.get(Calendar.MINUTE));
        }
        System.out.println("Case 7:");
        System.out.println(expected);
        System.out.println(result);
        System.out.println();

        //Case 8
        input = "beşe çeyrek kala";
        timeCal = speechAnalyst.getTime(input);
        expected = "Expected: 16:45";
        if (timeCal == null){
            result = "Result: No Result";
        }
        else {
            result = String.format("%02d", timeCal.get(Calendar.HOUR_OF_DAY)) + ":"+ String.format("%02d", timeCal.get(Calendar.MINUTE));
        }
        System.out.println("Case 8:");
        System.out.println(expected);
        System.out.println(result);
        System.out.println();

        //Case 9
        input = "sabah dörtte";
        timeCal = speechAnalyst.getTime(input);
        expected = "Expected: 04:00";
        if (timeCal == null){
            result = "Result: No Result";
        }
        else {
            result = String.format("%02d", timeCal.get(Calendar.HOUR_OF_DAY)) + ":"+ String.format("%02d", timeCal.get(Calendar.MINUTE));
        }
        System.out.println("Case 9:");
        System.out.println(expected);
        System.out.println(result);
        System.out.println();

        //Case 10
        input = "sabah dördü beş geçe";
        timeCal = speechAnalyst.getTime(input);
        expected = "Expected: 04:05";
        if (timeCal == null){
            result = "Result: No Result";
        }
        else {
            result = String.format("%02d", timeCal.get(Calendar.HOUR_OF_DAY)) + ":"+ String.format("%02d", timeCal.get(Calendar.MINUTE));
        }
        System.out.println("Case 10:");
        System.out.println(expected);
        System.out.println(result);
        System.out.println();

        //Case 11
        input = "merhaba";
        timeCal = speechAnalyst.getTime(input);
        expected = "Expected: No Result";
        if (timeCal == null){
            result = "Result: No Result";
        }
        else {
            result = String.format("%02d", timeCal.get(Calendar.HOUR_OF_DAY)) + ":"+ String.format("%02d", timeCal.get(Calendar.MINUTE));
        }
        System.out.println("Case 11:");
        System.out.println(expected);
        System.out.println(result);
        System.out.println();

        //Case 12
        input = "onu otuz beş geçe";
        timeCal = speechAnalyst.getTime(input);
        expected = "Expected: 10:55";
        if (timeCal == null){
            result = "Result: No Result";
        }
        else {
            result = String.format("%02d", timeCal.get(Calendar.HOUR_OF_DAY)) + ":"+ String.format("%02d", timeCal.get(Calendar.MINUTE));
        }
        System.out.println("Case 12:");
        System.out.println(expected);
        System.out.println(result);
        System.out.println();

        //Case 13
        input = "on bir buçuk";
        timeCal = speechAnalyst.getTime(input);
        expected = "Expected: 11:30";
        if (timeCal == null){
            result = "Result: No Result";
        }
        else {
            result = String.format("%02d", timeCal.get(Calendar.HOUR_OF_DAY)) + ":"+ String.format("%02d", timeCal.get(Calendar.MINUTE));
        }
        System.out.println("Case 13:");
        System.out.println(expected);
        System.out.println(result);
        System.out.println();

        //Case 14
        input = "onbir buçuk";
        timeCal = speechAnalyst.getTime(input);
        expected = "Expected: 11:30";
        if (timeCal == null){
            result = "Result: No Result";
        }
        else {
            result = String.format("%02d", timeCal.get(Calendar.HOUR_OF_DAY)) + ":"+ String.format("%02d", timeCal.get(Calendar.MINUTE));
        }
        System.out.println("Case 14:");
        System.out.println(expected);
        System.out.println(result);
        System.out.println();


    }

    public void performDateTests(SpeechAnalyst speechAnalyst){
        Calendar dateCal;
        String expected;
        String result;
        String input;

        //Case 1
        input = "Bugün";
        dateCal = speechAnalyst.getDate(input);
        expected = "Expected: 31/12/2016";
        if (dateCal == null){
            result = "Result: No Result";
        }
        else {
            result = "Result: "+ String.format("%02d", dateCal.get(Calendar.DAY_OF_MONTH)) + "/"+ String.format("%02d", dateCal.get(Calendar.MONTH)+1)+"/"+String.format("%02d", dateCal.get(Calendar.YEAR));
        }System.out.println("Case 1:");
        System.out.println(expected);
        System.out.println(result);
        System.out.println();

        //Case 2
        input = "yarına";
        dateCal = speechAnalyst.getDate(input);
        expected = "Expected: 01/01/2017";
        if (dateCal == null){
            result = "Result: No Result";
        }
        else {
            result = "Result: "+ String.format("%02d", dateCal.get(Calendar.DAY_OF_MONTH)) + "/"+ String.format("%02d", dateCal.get(Calendar.MONTH)+1)+"/"+String.format("%02d", dateCal.get(Calendar.YEAR));
        }System.out.println("Case 2:");
        System.out.println(expected);
        System.out.println(result);
        System.out.println();

        //Case 3
        input = "çarşambaya";
        dateCal = speechAnalyst.getDate(input);
        expected = "Expected: 04/01/2017";
        if (dateCal == null){
            result = "Result: No Result";
        }
        else {
            result = "Result: "+ String.format("%02d", dateCal.get(Calendar.DAY_OF_MONTH)) + "/"+ String.format("%02d", dateCal.get(Calendar.MONTH)+1)+"/"+String.format("%02d", dateCal.get(Calendar.YEAR));
        }System.out.println("Case 3:");
        System.out.println(expected);
        System.out.println(result);
        System.out.println();

        //Case 4
        input = "gelecek haftaya";
        dateCal = speechAnalyst.getDate(input);
        expected = "Expected: 07/01/2017";
        if (dateCal == null){
            result = "Result: No Result";
        }
        else {
            result = "Result: "+ String.format("%02d", dateCal.get(Calendar.DAY_OF_MONTH)) + "/"+ String.format("%02d", dateCal.get(Calendar.MONTH)+1)+"/"+String.format("%02d", dateCal.get(Calendar.YEAR));
        }System.out.println("Case 4:");
        System.out.println(expected);
        System.out.println(result);
        System.out.println();

        //Case 5
        input = "bu ayın 29'una";
        dateCal = speechAnalyst.getDate(input);
        expected = "Expected: 29/12/2016";
        if (dateCal == null){
            result = "Result: No Result";
        }
        else {
            result = "Result: "+ String.format("%02d", dateCal.get(Calendar.DAY_OF_MONTH)) + "/"+ String.format("%02d", dateCal.get(Calendar.MONTH)+1)+"/"+String.format("%02d", dateCal.get(Calendar.YEAR));
        }System.out.println("Case 5:");
        System.out.println(expected);
        System.out.println(result);
        System.out.println();

        //Case 6
        input = "gelecek ayın beşine";
        dateCal = speechAnalyst.getDate(input);
        expected = "Expected: 05/01/2017";
        if (dateCal == null){
            result = "Result: No Result";
        }
        else {
            result = "Result: "+ String.format("%02d", dateCal.get(Calendar.DAY_OF_MONTH)) + "/"+ String.format("%02d", dateCal.get(Calendar.MONTH)+1)+"/"+String.format("%02d", dateCal.get(Calendar.YEAR));
        } System.out.println("Case 6:");
        System.out.println(expected);
        System.out.println(result);
        System.out.println();

        //Case 7
        input = "11 mart 2018 e";
        dateCal = speechAnalyst.getDate(input);
        expected = "Expected: 11/03/2018";
        if (dateCal == null){
            result = "Result: No Result";
        }
        else {
            result = "Result: "+ String.format("%02d", dateCal.get(Calendar.DAY_OF_MONTH)) + "/"+ String.format("%02d", dateCal.get(Calendar.MONTH)+1)+"/"+String.format("%02d", dateCal.get(Calendar.YEAR));
        }   System.out.println("Case 7:");
        System.out.println(expected);
        System.out.println(result);
        System.out.println();

        //Case 8
        input = "11 marta";
        dateCal = speechAnalyst.getDate(input);
        expected = "Expected: 11/03/2017";
        if (dateCal == null){
            result = "Result: No Result";
        }
        else {
            result = "Result: "+ String.format("%02d", dateCal.get(Calendar.DAY_OF_MONTH)) + "/"+ String.format("%02d", dateCal.get(Calendar.MONTH)+1)+"/"+String.format("%02d", dateCal.get(Calendar.YEAR));
        }System.out.println("Case 8:");
        System.out.println(expected);
        System.out.println(result);
        System.out.println();

        //Case 9
        input = "9 gün sonraya";
        dateCal = speechAnalyst.getDate(input);
        expected = "Expected: 09/01/2017";
        if (dateCal == null){
            result = "Result: No Result";
        }
        else {
            result = "Result: "+ String.format("%02d", dateCal.get(Calendar.DAY_OF_MONTH)) + "/"+ String.format("%02d", dateCal.get(Calendar.MONTH)+1)+"/"+String.format("%02d", dateCal.get(Calendar.YEAR));
        } System.out.println("Case 9:");
        System.out.println(expected);
        System.out.println(result);
        System.out.println();

        //Case 10
        input = "ekime";
        dateCal = speechAnalyst.getDate(input);
        expected = "Expected: 31/10/2017";
        if (dateCal == null){
            result = "Result: No Result";
        }
        else {
            result = "Result: "+ String.format("%02d", dateCal.get(Calendar.DAY_OF_MONTH)) + "/"+ String.format("%02d", dateCal.get(Calendar.MONTH)+1)+"/"+String.format("%02d", dateCal.get(Calendar.YEAR));
        }
        System.out.println("Case 10:");
        System.out.println(expected);
        System.out.println(result);
        System.out.println();

        //Case 11
        input = "gelecek hafta çarşambaya";
        dateCal = speechAnalyst.getDate(input);
        expected = "Expected: 11/01/2017";
        if (dateCal == null){
            result = "Result: No Result";
        }
        else {
            result = "Result: "+ String.format("%02d", dateCal.get(Calendar.DAY_OF_MONTH)) + "/"+ String.format("%02d", dateCal.get(Calendar.MONTH)+1)+"/"+String.format("%02d", dateCal.get(Calendar.YEAR));
        }
        System.out.println("Case 11:");
        System.out.println(expected);
        System.out.println(result);
        System.out.println();

        //Case 12
        input = "bu hafta salıya";
        dateCal = speechAnalyst.getDate(input);
        expected = "Expected: 03/01/2017";
        if (dateCal == null){
            result = "Result: No Result";
        }
        else {
            result = "Result: "+ String.format("%02d", dateCal.get(Calendar.DAY_OF_MONTH)) + "/"+ String.format("%02d", dateCal.get(Calendar.MONTH)+1)+"/"+String.format("%02d", dateCal.get(Calendar.YEAR));
        }
        System.out.println("Case 12:");
        System.out.println(expected);
        System.out.println(result);
        System.out.println();

    }

    public void performConfirmationTests(SpeechAnalyst speechAnalyst) {
        boolean result;
        String expectedStr;
        String resultStr;
        String inputStr;

        //Case 1
        inputStr = "evet oluştur";
        result = speechAnalyst.confirmationGiven(inputStr);
        expectedStr = "Expected: true";
        if (result){
            resultStr = "Result: true";
        }
        else {
            resultStr = "Result: false";
        }
        System.out.println("Case 1:");
        System.out.println("Input: " + inputStr);
        System.out.println(expectedStr);
        System.out.println(resultStr);
        System.out.println("\n");

        //Case 2
        inputStr = "evet onaylıyorum";
        result = speechAnalyst.confirmationGiven(inputStr);
        expectedStr = "Expected: true";
        if (result){
            resultStr = "Result: true";
        }
        else {
            resultStr = "Result: false";
        }
        System.out.println("Case 2:");
        System.out.println("Input: " + inputStr);
        System.out.println(expectedStr);
        System.out.println(resultStr);
        System.out.println("\n");


        //Case 3
        inputStr = "etkinliği kaydet";
        result = speechAnalyst.confirmationGiven(inputStr);
        expectedStr = "Expected: false";
        if (result){
            resultStr = "Result: true";
        }
        else {
            resultStr = "Result: false";
        }
        System.out.println("Case 3:");
        System.out.println("Input: " + inputStr);
        System.out.println(expectedStr);
        System.out.println(resultStr);
        System.out.println("\n");


        //Case 4
        inputStr = "hayır etkinliği iptal et";
        result = speechAnalyst.confirmationGiven(inputStr);
        expectedStr = "Expected: false";
        if (result){
            resultStr = "Result: true";
        }
        else {
            resultStr = "Result: false";
        }
        System.out.println("Case 4:");
        System.out.println("Input: " + inputStr);
        System.out.println(expectedStr);
        System.out.println(resultStr);
        System.out.println("\n");


        //Case 5
        inputStr = "evet etkinliği kaydetmek istiyorum";
        result = speechAnalyst.confirmationGiven(inputStr);
        expectedStr = "Expected: true";
        if (result){
            resultStr = "Result: true";
        }
        else {
            resultStr = "Result: false";
        }
        System.out.println("Case 5:");
        System.out.println("Input: " + inputStr);
        System.out.println(expectedStr);
        System.out.println(resultStr);
        System.out.println("\n");


        //Case 6
        inputStr = "onaylıyorum değiştir";
        result = speechAnalyst.confirmationGiven(inputStr);
        expectedStr = "Expected: true";
        if (result){
            resultStr = "Result: true";
        }
        else {
            resultStr = "Result: false";
        }
        System.out.println("Case 6:");
        System.out.println("Input: " + inputStr);
        System.out.println(expectedStr);
        System.out.println(resultStr);
        System.out.println("\n");


        //Case 7
        inputStr = "hayır etkinliği kaydetmek istiyorum";
        result = speechAnalyst.confirmationGiven(inputStr);
        expectedStr = "Expected: true";
        if (result){
            resultStr = "Result: true";
        }
        else {
            resultStr = "Result: false";
        }
        System.out.println("Case 7:");
        System.out.println("Input: " + inputStr);
        System.out.println(expectedStr);
        System.out.println(resultStr);
        System.out.println("\n");


        //Case 8
        inputStr = "hayır iptal et";
        result = speechAnalyst.confirmationGiven(inputStr);
        expectedStr = "Expected: false";
        if (result){
            resultStr = "Result: true";
        }
        else {
            resultStr = "Result: false";
        }
        System.out.println("Case 8:");
        System.out.println("Input: " + inputStr);
        System.out.println(expectedStr);
        System.out.println(resultStr);
        System.out.println("\n");


        //Case 9
        inputStr = "evet iptal et";
        result = speechAnalyst.confirmationGiven(inputStr);
        expectedStr = "Expected: true";
        if (result){
            resultStr = "Result: true";
        }
        else {
            resultStr = "Result: false";
        }
        System.out.println("Case 9:");
        System.out.println("Input: " + inputStr);
        System.out.println(expectedStr);
        System.out.println(resultStr);
        System.out.println("\n");


        //Case 10
        inputStr = "evet eminim iptal et";
        result = speechAnalyst.confirmationGiven(inputStr);
        expectedStr = "Expected: true";
        if (result){
            resultStr = "Result: true";
        }
        else {
            resultStr = "Result: false";
        }
        System.out.println("Case 10:");
        System.out.println("Input: " + inputStr);
        System.out.println(expectedStr);
        System.out.println(resultStr);
        System.out.println("\n");

    }

    public void performRejectionTests(SpeechAnalyst speechAnalyst) {
        boolean result;
        String expectedStr;
        String resultStr;
        String inputStr;

        //Case 1
        inputStr = "evet oluştur";
        result = speechAnalyst.rejectionGiven(inputStr);
        expectedStr = "Expected: false";
        if (result){
            resultStr = "Result: true";
        }
        else {
            resultStr = "Result: false";
        }
        System.out.println("Case 1:");
        System.out.println("Input: " + inputStr);
        System.out.println(expectedStr);
        System.out.println(resultStr);
        System.out.println("\n");

        //Case 2
        inputStr = "evet onaylıyorum";
        result = speechAnalyst.rejectionGiven(inputStr);
        expectedStr = "Expected: false";
        if (result){
            resultStr = "Result: true";
        }
        else {
            resultStr = "Result: false";
        }
        System.out.println("Case 2:");
        System.out.println("Input: " + inputStr);
        System.out.println(expectedStr);
        System.out.println(resultStr);
        System.out.println("\n");


        //Case 3
        inputStr = "etkinliği kaydet";
        result = speechAnalyst.rejectionGiven(inputStr);
        expectedStr = "Expected: false";
        if (result){
            resultStr = "Result: true";
        }
        else {
            resultStr = "Result: false";
        }
        System.out.println("Case 3:");
        System.out.println("Input: " + inputStr);
        System.out.println(expectedStr);
        System.out.println(resultStr);
        System.out.println("\n");


        //Case 4
        inputStr = "hayır etkinliği iptal et";
        result = speechAnalyst.rejectionGiven(inputStr);
        expectedStr = "Expected: true";
        if (result){
            resultStr = "Result: true";
        }
        else {
            resultStr = "Result: false";
        }
        System.out.println("Case 4:");
        System.out.println("Input: " + inputStr);
        System.out.println(expectedStr);
        System.out.println(resultStr);
        System.out.println("\n");


        //Case 5
        inputStr = "evet etkinliği kaydetmek istiyorum";
        result = speechAnalyst.rejectionGiven(inputStr);
        expectedStr = "Expected: false";
        if (result){
            resultStr = "Result: true";
        }
        else {
            resultStr = "Result: false";
        }
        System.out.println("Case 5:");
        System.out.println("Input: " + inputStr);
        System.out.println(expectedStr);
        System.out.println(resultStr);
        System.out.println("\n");


        //Case 6
        inputStr = "onaylıyorum değiştir";
        result = speechAnalyst.rejectionGiven(inputStr);
        expectedStr = "Expected: false";
        if (result){
            resultStr = "Result: true";
        }
        else {
            resultStr = "Result: false";
        }
        System.out.println("Case 6:");
        System.out.println("Input: " + inputStr);
        System.out.println(expectedStr);
        System.out.println(resultStr);
        System.out.println("\n");


        //Case 7
        inputStr = "hayır etkinliği kaydetmek istiyorum";
        result = speechAnalyst.rejectionGiven(inputStr);
        expectedStr = "Expected: true";
        if (result){
            resultStr = "Result: true";
        }
        else {
            resultStr = "Result: false";
        }
        System.out.println("Case 7:");
        System.out.println("Input: " + inputStr);
        System.out.println(expectedStr);
        System.out.println(resultStr);
        System.out.println("\n");


        //Case 8
        inputStr = "hayır iptal et";
        result = speechAnalyst.rejectionGiven(inputStr);
        expectedStr = "Expected: true";
        if (result){
            resultStr = "Result: true";
        }
        else {
            resultStr = "Result: false";
        }
        System.out.println("Case 8:");
        System.out.println("Input: " + inputStr);
        System.out.println(expectedStr);
        System.out.println(resultStr);
        System.out.println("\n");


        //Case 9
        inputStr = "evet iptal et";
        result = speechAnalyst.rejectionGiven(inputStr);
        expectedStr = "Expected: true";
        if (result){
            resultStr = "Result: true";
        }
        else {
            resultStr = "Result: false";
        }
        System.out.println("Case 9:");
        System.out.println("Input: " + inputStr);
        System.out.println(expectedStr);
        System.out.println(resultStr);
        System.out.println("\n");


        //Case 10
        inputStr = "evet eminim iptal et";
        result = speechAnalyst.rejectionGiven(inputStr);
        expectedStr = "Expected: true";
        if (result){
            resultStr = "Result: true";
        }
        else {
            resultStr = "Result: false";
        }
        System.out.println("Case 10:");
        System.out.println("Input: " + inputStr);
        System.out.println(expectedStr);
        System.out.println(resultStr);
        System.out.println("\n");

    }

    public void performCommandTests(SpeechAnalyst speechAnalyst) {

    }

    public void performNumberTests(SpeechAnalyst speechAnalyst){
        int result;
        String expectedStr;
        String resultStr;
        String inputStr;

        //Case 1
        inputStr = "bir";
        result = speechAnalyst.getNumber(inputStr);
        expectedStr = "Expected: 1";
        if (result != -1){
            resultStr = "Result: " + Integer.toString(result);
        }
        else {
            resultStr = "Result: No number";
        }
        System.out.println("Case 1:");
        System.out.println("Input: " + inputStr);
        System.out.println(expectedStr);
        System.out.println(resultStr);
        System.out.println("\n");


        //Case 2
        inputStr = "1";
        result = speechAnalyst.getNumber(inputStr);
        expectedStr = "Expected: 1";
        if (result != -1){
            resultStr = "Result: " + Integer.toString(result);
        }
        else {
            resultStr = "Result: No number";
        }
        System.out.println("Case 2:");
        System.out.println("Input: " + inputStr);
        System.out.println(expectedStr);
        System.out.println(resultStr);
        System.out.println("\n");


        //Case 3
        inputStr = "ellibeş";
        result = speechAnalyst.getNumber(inputStr);
        expectedStr = "Expected: 55";
        if (result != -1){
            resultStr = "Result: " + Integer.toString(result);
        }
        else {
            resultStr = "Result: No number";
        }
        System.out.println("Case 3:");
        System.out.println("Input: " + inputStr);
        System.out.println(expectedStr);
        System.out.println(resultStr);
        System.out.println("\n");


        //Case 4
        inputStr = "kırk";
        result = speechAnalyst.getNumber(inputStr);
        expectedStr = "Expected: 40";
        if (result != -1){
            resultStr = "Result: " + Integer.toString(result);
        }
        else {
            resultStr = "Result: No number";
        }
        System.out.println("Case 4:");
        System.out.println("Input: " + inputStr);
        System.out.println(expectedStr);
        System.out.println(resultStr);
        System.out.println("\n");


        //Case 5
        inputStr = "95";
        result = speechAnalyst.getNumber(inputStr);
        expectedStr = "Expected: 95";
        if (result != -1){
            resultStr = "Result: " + Integer.toString(result);
        }
        else {
            resultStr = "Result: No number";
        }
        System.out.println("Case 5:");
        System.out.println("Input: " + inputStr);
        System.out.println(expectedStr);
        System.out.println(resultStr);
        System.out.println("\n");


        //Case 6
        inputStr = "there is no number in this string";
        result = speechAnalyst.getNumber(inputStr);
        expectedStr = "Expected: No number";
        if (result != -1){
            resultStr = "Result: " + Integer.toString(result);
        }
        else {
            resultStr = "Result: No number";
        }
        System.out.println("Case 6:");
        System.out.println("Input: " + inputStr);
        System.out.println(expectedStr);
        System.out.println(resultStr);
        System.out.println("\n");


        //Case 7
        inputStr = "otuz dokuz";
        result = speechAnalyst.getNumber(inputStr);
        expectedStr = "Expected: 39";
        if (result != -1){
            resultStr = "Result: " + Integer.toString(result);
        }
        else {
            resultStr = "Result: No number";
        }
        System.out.println("Case 7:");
        System.out.println("Input: " + inputStr);
        System.out.println(expectedStr);
        System.out.println(resultStr);
        System.out.println("\n");


        //Case 8
        inputStr = "on üç";
        result = speechAnalyst.getNumber(inputStr);
        expectedStr = "Expected: 13";
        if (result != -1){
            resultStr = "Result: " + Integer.toString(result);
        }
        else {
            resultStr = "Result: No number";
        }
        System.out.println("Case 8:");
        System.out.println("Input: " + inputStr);
        System.out.println(expectedStr);
        System.out.println(resultStr);
        System.out.println("\n");


        //Case 9
        inputStr = "2017";
        result = speechAnalyst.getNumber(inputStr);
        expectedStr = "Expected: 2017";
        if (result != -1){
            resultStr = "Result: " + Integer.toString(result);
        }
        else {
            resultStr = "Result: No number";
        }
        System.out.println("Case 9:");
        System.out.println("Input: " + inputStr);
        System.out.println(expectedStr);
        System.out.println(resultStr);
        System.out.println("\n");


        //Case 10
        inputStr = "çeyrek";
        result = speechAnalyst.getNumber(inputStr);
        expectedStr = "Expected: 15";
        if (result != -1){
            resultStr = "Result: " + Integer.toString(result);
        }
        else {
            resultStr = "Result: No number";
        }
        System.out.println("Case 10:");
        System.out.println("Input: " + inputStr);
        System.out.println(expectedStr);
        System.out.println(resultStr);
        System.out.println("\n");


        //Case 11
        inputStr = "buçuğa";
        result = speechAnalyst.getNumber(inputStr);
        expectedStr = "Expected: 30";
        if (result != -1){
            resultStr = "Result: " + Integer.toString(result);
        }
        else {
            resultStr = "Result: No number";
        }
        System.out.println("Case 11:");
        System.out.println("Input: " + inputStr);
        System.out.println(expectedStr);
        System.out.println(resultStr);
        System.out.println("\n");
    }
}
