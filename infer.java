import java.util.*;
import java.io.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.util.FileUtils;
import org.apache.jena.ontology.*;

    //INTERESSANTE!!! QUESTI SONO DI TopBraid, NON DI JENA!
    //Ho provato ad usare quelle di Jena, ma non sono riuscito, sollevavano un vespaio di eccezioni...
import org.topbraid.jenax.util.JenaUtil;
import org.topbraid.shacl.rules.RuleUtil;

import org.topbraid.shacl.util.ModelPrinter;

public class infer 
{
    private static File ontologyFile = new File("ontology.rdf");
    private static File rulesFile = new File("rules.rdf");
    private static File inferredTriplesFile = new File("inferredTriples.rdf");
    private static File inferredOntologyFile = new File("inferredOntology.rdf");
    
        //RICORDA SEMPRE: SHACL È SOLO PER VALIDARE RDF, NON OWL!!! SHACL è UN'ESTENSIONE DI RDF, NON DI OWL!!!
    public static void main(String[] args) throws Exception 
    {
        if(args.length==2)
        {
            ontologyFile = new File(args[0]);
            rulesFile = new File(args[1]);
        }
        
            //Load the riolOntology
        Model ontology = JenaUtil.createMemoryModel();
        FileInputStream fis = new FileInputStream(ontologyFile);
        ontology.read(fis, "urn:dummy", FileUtils.langTurtle);
        fis.close();
        
            //Inference regulative rules
        Model rules = JenaUtil.createMemoryModel();
        fis = new FileInputStream(rulesFile);
        rules.read(fis, "urn:dummy", FileUtils.langTurtle);
        fis.close();

        Model inferredModel = RuleUtil.executeRules(ontology, rules, null, null);
        System.out.println(ModelPrinter.get().print(inferredModel));
        
        writeAll(inferredModel);
    }
    
    private static void writeAll(Model model)throws Exception
    {
        while(inferredTriplesFile.exists())inferredTriplesFile.delete();
        PrintStream psIT = new PrintStream(inferredTriplesFile);
        
        while(inferredOntologyFile.exists())inferredOntologyFile.delete();
        PrintStream psIO = new PrintStream(inferredOntologyFile);
        
        InputStream inputStream = new FileInputStream(ontologyFile);
        BufferedReader reader = new java.io.BufferedReader(new InputStreamReader(inputStream));
        String buffer = null;
        while((buffer=reader.readLine())!=null)psIO.println(buffer);
        psIO.println();
        psIO.println("#INFERRED TRIPLES");
        psIO.println();
        
        StmtIterator iter = model.listStatements();
        
        String[][] prefixes = {
            {"owl:","http://www.w3.org/2002/07/owl#"},
            {"rdf:","http://www.w3.org/1999/02/22-rdf-syntax-ns#"},
            {"rdfs:","http://www.w3.org/2000/01/rdf-schema#"},
            {"xsd:","http://www.w3.org/2001/XMLSchema#"},
            {":","https://www.swansea.ac.uk/law/aiandlawapplication/kuuku/gasoil2#"}};

        while(iter.hasNext())
        {
            Statement s = iter.next();
            String a = s.getSubject().toString()+" "+s.getPredicate().toString()+" "+s.getObject().toString();
            
            String salvaA=a;
            
            for(int i=0;i<prefixes.length;i++)
                if(a.indexOf(prefixes[i][1])!=-1)
                    a=a.replaceAll(prefixes[i][1], prefixes[i][0]);
            
                //The number needs to be enclosed between "...", but when I print the s.getObject it does not add the "..."
            if(a.indexOf("^^xsd:integer")!=-1)
                a = a.substring(0, a.lastIndexOf(" "))+
                        " \""+a.substring(a.lastIndexOf(" ")+1, a.lastIndexOf("^^xsd:integer"))+"\"^^xsd:integer";
            
            psIT.println(a+".");
            psIO.println(a+".");
        }
        psIT.flush();
        psIT.close();
    }
}