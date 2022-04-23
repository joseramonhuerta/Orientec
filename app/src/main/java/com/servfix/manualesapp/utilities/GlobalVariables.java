package com.servfix.manualesapp.utilities;

import java.util.HashMap;

public class GlobalVariables {
    public static final String URLServicio = "https://erp.servfix.com.mx/ws/manualesapp/";

    //PAYPAL
    public static final String PAYPAL_CLIENT_ID = "AccHfI2JChSGd786mGBU1HRMl1L4VQg5KljkjVUwWrJWfxgclKM29ZUl7XXnobxzbjdNaqXrw6I8ZGhG"; //Produccion
    //public static final String PAYPAL_CLIENT_ID = "AdfLydJA7y9MrwEWOax033Z2MinL6gpEf3-NiqQ5A_ngLdWgUkiuw83R61PfNq741rhLrzxJ2Hqs3qPp"; //SANDBOX

    public static  final String STRIPE_CLIENT_ID = "sk_test_IKYCHOAmUhC7IPTdaoVtO58D";
    public static  final String STRIPE_TEST_KEY = "pk_test_51ItcdaJc0kH60q11ZGJJ48BSDQtWAjzNb1TsbZqoUDE07Jrv38XKWF2Nkkqne4tj0KDZjzPwdvGgaCjxxeDWN3te00aZy4QMuT"; //test
    public static  final String STRIPE_PRODUCTION_KEY = "pk_live_51ItcdaJc0kH60q11J0rh7a6dqlbiybDyKpDbsN8dre1tKk7NGgBIXcQX93KvWEZFj2d2ugdMFcBF8MWSPJuCjy3j00Gj0Etsqf"; //produccion

    //OPENPAY(PAYNET)
    public static final String MERCHANT_ID_SANDBOX = "meousvsdgiet2ka7px79";
    public static final String MERCHANT_ID_PRODUCCION = "";
    public static final String PAYNET_SK_SANDBOX = "sk_a6cf930308bf4a07b199777e9cbc4744";
    public static final String PAYNET_SK_PRODUCCION = "";

    public static int id_usuario;
    public static String usuario;
    public static String nombre_usuario;
    public static String paterno;
    public static String materno;
    public static String celular;
    public static int tipo_usuario;
    public static String id_usuario_firebase;
    public static String fcmToken;
    public static String imagen;
    public static String conocimientos_tecnicos;
    public static String beneficiario;
    public static String cuenta_bancaria;

}
