package edu.eci.arep.sparkwebapp.Services;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class cipherServices {

    public static String convertPass(String pass) {

        MessageDigest md = null;
        try {

            // Static getInstance method is called with hashing MD5
             md = MessageDigest.getInstance("MD5");
        }
            catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

            // digest() method is called to calculate message digest
            //  of an input digest() return array of byte
            byte[] messageDigest = md.digest(pass.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }


    public static Boolean authenticate(String password){
        return password.equals("gl.a");
    }
}
