
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import javax.xml.ws.Holder;
import mx.bigdata.sat.cfdi.CFDv32;
import mx.bigdata.sat.cfdi.v32.schema.Comprobante;
import mx.bigdata.sat.security.KeyLoader;
import mx.bigdata.sat.security.PrivateKeyLoader;
import mx.bigdata.sat.security.PublicKeyLoader;
import mx.bigdata.sat.cfdi.schema.ObjectFactory;
import mx.bigdata.sat.cfdi.v32.schema.TUbicacionFiscal;
import mx.bigdata.sat.cfdi.v32.schema.Comprobante.*;
import mx.bigdata.sat.cfdi.v32.schema.Comprobante.Emisor.RegimenFiscal;
import mx.bigdata.sat.cfdi.v32.schema.TUbicacion;
import mx.bigdata.sat.cfdi.v32.schema.TimbreFiscalDigital;
import sun.misc.BASE64Encoder;
import webservice.*;
import webservice_cancelacion.*;

public class Main {
    
    static String keystorePath = "keystore.jks";
    private static String truststorePath = "truststore.jks";
    private static String rfc = "PPL961114GZ1";
    static String password = "prueba";
    private static String filePath = "archivo.xml";
    private static Holder<AcuseRespuestaServicio> holder = new Holder<>();
    
    
    static void setProperties(){
        System.getProperties().put("javax.net.debug","all");
        System.getProperties().put("javax.net.ssl.keyStore", keystorePath);
        System.getProperties().put("javax.net.ssl.keyStorePassword", password);
        System.getProperties().put("javax.net.ssl.keyStoreType","jks");
        System.getProperties().put("javax.net.ssl.trustStore", truststorePath);
        System.getProperties().put("javax.net.ssl.trustStoreType","jks");
        System.getProperties().put("javax.net.ssl.trustStorePassword", password);
    }
    
    public static void main(String[] args){
        
        setProperties();
        Scanner scanner = new Scanner(System.in);
        
        //Se genera la keystore y truststore en caso de que no se encuentren
        if(checarKeyStore()){
            System.out.println("Existe keystore");
        }else{
            System.out.println("No Existe keystore");
            System.out.println("Generando keystore");
            try{
                generarKeyStore();
                generarTrustStore();
            }catch(java.lang.Exception e){
                e.printStackTrace();
            }
        }
        
        String arg = "";
        do{
            System.out.println("arg");
            arg = scanner.nextLine().toLowerCase();
            switch(arg){
                case "crearcfdi":
                    try{
                    CFDI.generarCFDI(filePath);
                    }catch(java.lang.Exception e){
                        e.printStackTrace();
                    }
                    break;
            }
        }while(!arg.toLowerCase().equals("exit") || arg.equals("") || arg == null);
       

        
        
        try{
            //generarCFDI(filePath);
            /*Peticion peticion = new Peticion();            
            byte[] data = Files.readAllBytes(Paths.get(filePath));
            peticion.setArchivoATimbrar(data);
            timbrarRespStream(peticion);*/
//            PeticionString peticion = new PeticionString();
//            JAXBElement<String> el = new JAXBElement<String>();
//            peticion.setRutaArchivo();
//            descargarTimbrarRespStream(peticion);
            //cancelar(new PeticionCancelacionTimbrado());
        
        }catch(java.lang.Exception e){
            e.printStackTrace();
        }
    }
    
    static boolean checarKeyStore(){
        File f = new File(keystorePath);
        return f.exists();
    }
    
    
    
    private static void generarKeyStore() throws KeyStoreException,IOException, NoSuchAlgorithmException, CertificateException, NoSuchProviderException{
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        char[] pass = password.toCharArray();
        ks.load(null, pass);
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        BufferedInputStream b = new BufferedInputStream(new FileInputStream("fiel.crt"));
        Certificate certificate = cf.generateCertificate(b);
        FileOutputStream f = new FileOutputStream(keystorePath);
        ks.setCertificateEntry("fiel", certificate);
        ks.store(f,pass);
        b.close();
        f.close(); 
    }
    
    static void generarTrustStore()throws KeyStoreException,IOException, NoSuchAlgorithmException, CertificateException, NoSuchProviderException{
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        char[] pass = password.toCharArray();
        ks.load(null, pass);
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        BufferedInputStream b = new BufferedInputStream(new FileInputStream("servidor.cer"));
        Certificate certificate = cf.generateCertificate(b);
        FileOutputStream f = new FileOutputStream(truststorePath);
        ks.setCertificateEntry("servidor", certificate);
        ks.store(f,pass);
        b.close();
        f.close();
    }

    private static Respuesta descargarTimbrarRespStream(PeticionString parameters) throws java.lang.Exception {
        ServTimbrado service = new ServTimbrado(new URL("https://intgtimbradorecepcion.cloudapp.net/ServTimbrado.svc"));
        MessageHandlerResolver handlerResolver = new MessageHandlerResolver();
        service.setHandlerResolver(handlerResolver);
        IServTimbrado port = service.getEndPointRecibe();
        return port.descargarTimbrarRespStream(parameters,rfc,holder);
    }

    private static Respuesta timbrarRespStream(Peticion parameters) throws java.lang.Exception {
        ServTimbrado service = new ServTimbrado(new URL("https://intgtimbradorecepcion.cloudapp.net/ServTimbrado.svc"));
        MessageHandlerResolver handlerResolver = new MessageHandlerResolver();
        service.setHandlerResolver(handlerResolver);
        IServTimbrado port = service.getEndPointRecibe();
        return port.timbrarRespStream(parameters,rfc,holder);
    }

    private static RespuestaCancelacion cancelar(PeticionCancelacionTimbrado parameters) throws java.lang.Exception {
        ServTimbradoCan service = new ServTimbradoCan(new URL("https://intgtimbradocancelacion.cloudapp.net/ServTimbradoCan.svc"));
        MessageHandlerResolver handlerResolver = new MessageHandlerResolver();
        service.setHandlerResolver(handlerResolver);
        IServTimbradoCan port = service.getEndPointRecibe();
        return port.cancelar(parameters,rfc);
    }
}
