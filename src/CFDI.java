
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Scanner;
import mx.bigdata.sat.cfdi.CFDv32;
import mx.bigdata.sat.cfdi.v32.schema.Comprobante;
import mx.bigdata.sat.cfdi.v32.schema.Comprobante.Addenda;
import mx.bigdata.sat.cfdi.v32.schema.Comprobante.Complemento;
import mx.bigdata.sat.cfdi.v32.schema.Comprobante.Conceptos;
import mx.bigdata.sat.cfdi.v32.schema.Comprobante.Conceptos.Concepto;
import mx.bigdata.sat.cfdi.v32.schema.Comprobante.Emisor;
import mx.bigdata.sat.cfdi.v32.schema.Comprobante.Emisor.RegimenFiscal;
import mx.bigdata.sat.cfdi.v32.schema.Comprobante.Impuestos;
import mx.bigdata.sat.cfdi.v32.schema.Comprobante.Impuestos.Traslados;
import mx.bigdata.sat.cfdi.v32.schema.Comprobante.Impuestos.Traslados.Traslado;
import mx.bigdata.sat.cfdi.v32.schema.Comprobante.Receptor;
import mx.bigdata.sat.cfdi.v32.schema.ObjectFactory;
import mx.bigdata.sat.cfdi.v32.schema.TUbicacion;
import mx.bigdata.sat.cfdi.v32.schema.TUbicacionFiscal;
import mx.bigdata.sat.security.PrivateKeyLoader;
import mx.bigdata.sat.security.PublicKeyLoader;

public class CFDI {
    
    private static Scanner s = new Scanner(System.in);
    
    public static void generarCFDI(String file) throws java.lang.Exception{
        Comprobante comp = new Comprobante();
        ObjectFactory of = new ObjectFactory();
        comp.setVersion("3.2");
        System.out.println("Forma de Pago");
        //comp.setFormaDePago("PAGO EN UNA SOLA EXHIBICION");
        comp.setFormaDePago(s.nextLine());
        System.out.println("Subtotal");
        //comp.setSubTotal(new BigDecimal("466.43"));
        comp.setSubTotal(new BigDecimal(s.nextFloat()));
        System.out.println("Total");
        comp.setTotal(new BigDecimal(s.nextFloat()));
        //comp.setTotal(new BigDecimal("488.50"));
        s = new Scanner(System.in);
        System.out.println("Tipo de comprobante");
        //comp.setTipoDeComprobante("ingreso");
        comp.setTipoDeComprobante(s.nextLine());
        System.out.println("Metodo de pago");
        //comp.setMetodoDePago("efectivo");
        comp.setMetodoDePago(s.nextLine());
        System.out.println("Lugar de expedicion");
        //comp.setLugarExpedicion("Mexico");
        comp.setLugarExpedicion(s.nextLine());
        System.out.println("Fecha (dd-mm-yyyy)");
        //comp.setFecha(new Date());
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        comp.setFecha(sdf.parse(s.nextLine()));
        
        //Receptor
        comp.setReceptor(generarReceptor(of));
        
        //Emisor
        comp.setEmisor(generarEmisor(of));
        
        //Conceptos
        comp.setConceptos(generarConceptos(of));
        
        //Impuestos
        comp.setImpuestos(generarImpuestos(of));
        
        //Addenda
        comp.setAddenda(generarAddenda(of));
        
        //Complementos
        comp.setComplemento(generarComplemento(of));
        
        CFDv32 cfd = new CFDv32(comp);
        PrivateKey key = new PrivateKeyLoader(new FileInputStream("archivo.key"),  "password").getKey();
        X509Certificate cert = new PublicKeyLoader((new FileInputStream("fiel.crt"))).getKey();
        Comprobante sellado = cfd.sellarComprobante(key, cert);
        cfd.validar(); 
        cfd.verificar();
        cfd.guardar(new FileOutputStream(file));
    }
    
    private static Receptor generarReceptor(ObjectFactory of){
        Receptor receptor = of.createComprobanteReceptor();
        System.out.println("Datos de Receptor");
        System.out.println("Nombre");
        //receptor.setNombre("JUAN PEREZ PEREZ");
        receptor.setNombre(s.nextLine());
        System.out.println("RFC");
        //receptor.setRfc("PEPJ8001019Q8");
        receptor.setRfc(s.nextLine());
        TUbicacion uf = of.createTUbicacion();
        System.out.println("Calle");
        //uf.setCalle("AV UNIVERSIDAD");
        uf.setCalle(s.nextLine());
        System.out.println("C.P");
        //uf.setCodigoPostal("04360");
        uf.setCodigoPostal(s.nextLine());
        System.out.println("Colonia");
        //uf.setColonia("COPILCO UNIVERSIDAD"); 
        uf.setColonia(s.nextLine()); 
        System.out.println("Estado");
        //uf.setEstado("DISTRITO FEDERAL"); 
        uf.setEstado(s.nextLine()); 
        System.out.println("Delegacion/Municipio");
        //uf.setMunicipio("COYOACAN"); 
        uf.setMunicipio(s.nextLine()); 
        System.out.println("No. exterior");
        //uf.setNoExterior("16 EDF 3"); 
        uf.setNoExterior(s.nextLine()); 
        System.out.println("No. interior");
        //uf.setNoInterior("DPTO 101"); 
        String noint = s.nextLine();
        if(!noint.equals("")){
            uf.setNoInterior(noint); 
        }
        System.out.println("Pais");
        //uf.setPais("Mexico"); 
        uf.setPais(s.nextLine()); 
        receptor.setDomicilio(uf);
        return receptor;
    }
    
    private static Emisor generarEmisor(ObjectFactory of){
        Emisor emisor = of.createComprobanteEmisor();
        System.out.println("Datos de Emisor");
        System.out.println("Nombre");
        //emisor.setNombre("PHARMA PLUS SA DE CV");
        emisor.setNombre(s.nextLine());
        System.out.println("RFC");
        //emisor.setRfc("PPL961114GZ1");
        emisor.setRfc(s.nextLine());
        TUbicacionFiscal uf = of.createTUbicacionFiscal();
        System.out.println("Ubicacion Fiscal");
        System.out.println("Calle");
        //uf.setCalle("AV UNIVERSIDAD");
        uf.setCalle(s.nextLine());
        System.out.println("C.P");
        //uf.setCodigoPostal("04360");
        uf.setCodigoPostal(s.nextLine());
        System.out.println("Colonia");
        //uf.setColonia("COPILCO UNIVERSIDAD"); 
        uf.setColonia(s.nextLine()); 
        System.out.println("Estado");
        //uf.setEstado("DISTRITO FEDERAL"); 
        uf.setEstado(s.nextLine()); 
        System.out.println("Delegacion/Municipio");
        //uf.setMunicipio("COYOACAN"); 
        uf.setMunicipio(s.nextLine()); 
        System.out.println("No. exterior");
        //uf.setNoExterior("16 EDF 3"); 
        uf.setNoExterior(s.nextLine()); 
        System.out.println("No. interior");
        //uf.setNoInterior("DPTO 101"); 
        String noint = s.nextLine();
        if(!noint.equals("")){
            uf.setNoInterior(noint); 
        }
        System.out.println("Pais");
        //uf.setPais("Mexico"); 
        uf.setPais(s.nextLine()); 
        emisor.setDomicilioFiscal(uf);
        TUbicacion u = of.createTUbicacion();
        System.out.println("Domicilio Fiscal");
        System.out.println("Calle");
        //u.setCalle("AV UNIVERSIDAD");
        u.setCalle(s.nextLine());
        System.out.println("C.P");
        //u.setCodigoPostal("04360");
        u.setCodigoPostal(s.nextLine());
        System.out.println("Colonia");
        //u.setColonia("COPILCO UNIVERSIDAD"); 
        u.setColonia(s.nextLine()); 
        System.out.println("Estado");
        //u.setEstado("DISTRITO FEDERAL"); 
        u.setEstado(s.nextLine()); 
        System.out.println("Delegacion/Municipio");
        //u.setMunicipio("COYOACAN"); 
        u.setMunicipio(s.nextLine()); 
        System.out.println("No. exterior");
        //u.setNoExterior("16 EDF 3"); 
        u.setNoExterior(s.nextLine()); 
        System.out.println("No. interior");
        //u.setNoInterior("DPTO 101"); 
        String nointd = s.nextLine();
        if(!nointd.equals("")){
            u.setNoInterior(noint); 
        }
        System.out.println("Pais");
        //u.setPais("Mexico"); 
        u.setPais(s.nextLine());  
        emisor.setExpedidoEn(u); 
        RegimenFiscal rf = of.createComprobanteEmisorRegimenFiscal();
        System.out.println("RegimenFiscal");
        //rf.setRegimen("simplificado");
        rf.setRegimen(s.nextLine());
        emisor.getRegimenFiscal().add(rf);
        return emisor;
    }

    private static Conceptos generarConceptos(ObjectFactory of) {
        Conceptos c = of.createComprobanteConceptos();
        List<Concepto> list = c.getConcepto();
        String o = "";
        do{
            s = new Scanner(System.in);
            System.out.println("Unidad de concepto (Dejar vacio para terminar)");
            o = s.nextLine();
            if(!o.equals("")){
                Concepto c1 = of.createComprobanteConceptosConcepto();
                c1.setUnidad(o);
                System.out.println("Importe");
                c1.setImporte(new BigDecimal(s.nextFloat()));
                System.out.println("Cantidad");
                c1.setCantidad(new BigDecimal(s.nextFloat()));
                s = new Scanner(System.in);
                System.out.println("Descripcion");
                c1.setDescripcion(s.nextLine());
                System.out.println("Valor unitario");
                c1.setValorUnitario(new BigDecimal(s.nextFloat()));
                list.add(c1);
            }
        }while(!o.equals(""));
        return c;
    }

    private static Impuestos generarImpuestos(ObjectFactory of) {
        Impuestos imps = of.createComprobanteImpuestos();
        Traslados trs = of.createComprobanteImpuestosTraslados();
        List<Traslado> list = trs.getTraslado(); 
        String o = "";
        do{
            s = new Scanner(System.in);
            System.out.println("Impuesto (Dejar vacio para terminar)");
            o = s.nextLine();
            if(!o.equals("")){
                Traslado t1 = of.createComprobanteImpuestosTrasladosTraslado();
                t1.setImpuesto(o);
                System.out.println("Importe");
                t1.setImporte(new BigDecimal(s.nextFloat()));
                System.out.println("Tasa");
                t1.setTasa(new BigDecimal(s.nextFloat()));
                list.add(t1);
            }
        }while(!o.equals(""));
        imps.setTraslados(trs);
        return imps;
    }

    private static Addenda generarAddenda(ObjectFactory of) {
        Addenda addenda = of.createComprobanteAddenda();
        
        return addenda;
    }

    private static Complemento generarComplemento(ObjectFactory of) {
        Complemento complemento = of.createComprobanteComplemento();
	  
	return complemento;
    }
}
