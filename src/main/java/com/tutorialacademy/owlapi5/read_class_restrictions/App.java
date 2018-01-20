package com.tutorialacademy.owlapi5.read_class_restrictions;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.stream.Collectors;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLObjectCardinalityRestriction;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectVisitor;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLQuantifiedObjectRestriction;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

public class App {
	
	public static void main( String[] args ) throws OWLOntologyCreationException, FileNotFoundException
    {
		// load ontology
    	OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
   		OWLOntology ontology = manager.loadOntologyFromOntologyDocument( new File( getRelativeResourcePath( "vehicle.owl" ) ) );
   		
   		System.out.println( "Read and classes their axioms...\n" );
   		
   		// get all classes in the ontology
   		for( OWLClass oc : ontology.classesInSignature().collect( Collectors.toSet() ) ) {
			System.out.println( "Class: " + oc.toString() );
			
			// get all axioms for each class
			for( OWLAxiom axiom : ontology.axioms( oc ).collect( Collectors.toSet() ) ) {
				System.out.println( "\tAxiom: " + axiom.toString() );
				
				// create an object visitor to get to the subClass restrictions
				axiom.accept( new OWLObjectVisitor() {
					
					// found the subClassOf axiom  
				    public void visit( OWLSubClassOfAxiom subClassAxiom ) {
				    	
				    	// create an object visitor to read the underlying (subClassOf) restrictions
				    	subClassAxiom.getSuperClass().accept( new OWLObjectVisitor() {
				    		
						    public void visit( OWLObjectSomeValuesFrom someValuesFromAxiom ) {
						    	printQuantifiedRestriction( oc, someValuesFromAxiom );
						    }
						    
						    public void visit( OWLObjectExactCardinality exactCardinalityAxiom ) {
						    	printCardinalityRestriction( oc, exactCardinalityAxiom );
						    }
						    
						    public void visit( OWLObjectMinCardinality minCardinalityAxiom ) {
						    	printCardinalityRestriction( oc, minCardinalityAxiom );
						    }
						    
						    public void visit( OWLObjectMaxCardinality maxCardinalityAxiom ) {
						    	printCardinalityRestriction( oc, maxCardinalityAxiom );
						    }
						    
						    // TODO: same for AllValuesFrom etc.
				    	});
				    }
				});
				
			}
			
			System.out.println();
   		}
    }
	
	public static void printQuantifiedRestriction( OWLClass oc, OWLQuantifiedObjectRestriction restriction ) {
    	System.out.println( "\t\tClass: " + oc.toString() );
    	System.out.println( "\t\tClassExpressionType: " + restriction.getClassExpressionType().toString() );
    	System.out.println( "\t\tProperty: "+ restriction.getProperty().toString() );
    	System.out.println( "\t\tObject: " + restriction.getFiller().toString() );
    	System.out.println();
	}
	
	public static void printCardinalityRestriction( OWLClass oc, OWLObjectCardinalityRestriction restriction ) {
    	System.out.println( "\t\tClass: " + oc.toString() );
    	System.out.println( "\t\tClassExpressionType: " + restriction.getClassExpressionType().toString() );
    	System.out.println( "\t\tCardinality: " + restriction.getCardinality() );
    	System.out.println( "\t\tProperty: "+ restriction.getProperty().toString() );
    	System.out.println( "\t\tObject: " + restriction.getFiller().toString() );
    	System.out.println();
	}
	
	// resolve maven specific path for resources
	public static String getRelativeResourcePath( String resource ) throws FileNotFoundException {
		if( resource == null || resource.equals("") ) throw new IllegalArgumentException( resource );
		
		URL url = App.class.getClassLoader().getResource( resource );
		
		if( url == null ) throw new FileNotFoundException( resource );
		
		return url.getPath();
	}
}
