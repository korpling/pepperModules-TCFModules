/**
 * Copyright 2009 Humboldt University of Berlin, INRIA.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 */
package org.corpus_tools.peppermodules.tcfModules;

import org.corpus_tools.pepper.impl.PepperImporterImpl;
import org.corpus_tools.pepper.modules.PepperImporter;
import org.corpus_tools.pepper.modules.PepperMapper;
import org.corpus_tools.pepper.modules.PepperModule;
import org.corpus_tools.pepper.modules.PepperModuleProperties;
import org.corpus_tools.pepper.modules.exceptions.PepperModuleNotReadyException;
import org.corpus_tools.salt.common.SCorpus;
import org.corpus_tools.salt.common.SDocument;
import org.corpus_tools.salt.graph.Identifier;
import org.eclipse.emf.common.util.URI;
import org.osgi.service.component.annotations.Component;

/**
 * This is a sample {@link PepperImporter}, which can be used for creating individual Importers for the 
 * Pepper Framework. Therefore you have to take a look to todo's and adapt the code.
 * 
 * <ul>
 *  <li>the salt model to fill, manipulate or export can be accessed via {@link #getSaltProject()}</li>
 * 	<li>special parameters given by Pepper workflow can be accessed via {@link #getSpecialParams()}</li>
 *  <li>a place to store temporary datas for processing can be accessed via {@link #getTemproraries()}</li>
 *  <li>a place where resources of this bundle are, can be accessed via {@link #getResources()}</li>
 *  <li>a logService can be accessed via {@link #getLogService()}</li>
 * </ul>
 * If this is the first time, you are implementing a Pepper module, we strongly recommend, to take a look into the
 * 'Developer's Guide for Pepper modules', you will find on <a href="https://korpling.german.hu-berlin.de/saltnpepper/">https://korpling.german.hu-berlin.de/saltnpepper/</a>.
 * @author Florian Zipser
 * @version 1.0
 *
 */
//TODO change the name of the component, for example use the format name and the ending Importer (FORMATImporterComponent)
@Component(name="TCFImporterComponent", factory="PepperImporterComponentFactory")
public class TCFImporter extends PepperImporterImpl implements PepperImporter
{

// =================================================== mandatory ===================================================
	/**
	 * <strong>OVERRIDE THIS METHOD FOR CUSTOMIZATION</strong>
	 * 
	 * A constructor for your module. Set the coordinates, with which your module shall be registered. 
	 * The coordinates (modules name, version and supported formats) are a kind of a fingerprint, 
	 * which should make your module unique.
	 */
	public TCFImporter()
	{
		super();
		this.setName("TCFImporter");
	    setSupplierContact(URI.createURI("saltnpepper@lists.hu-berlin.de"));
		setSupplierHomepage(URI.createURI("https://github.com/korpling/pepperModules-TCFModules"));
		setDesc("This importer transforms data in TCF format produced for instance by WebLicht (see http://weblicht.sfs.uni-tuebingen.de/) or WebAnno (see https://www.ukp.tu-darmstadt.de/software/webanno/) to a Salt model. ");
		addSupportedFormat("TCF", "0.4", null);
		setProperties(new TCFImporterProperties());
		getDocumentEndings().add("xml");
		getDocumentEndings().add("tcf");
	}
	
	/**
	 * <strong>OVERRIDE THIS METHOD FOR CUSTOMIZATION</strong>
	 * 
	 * This method creates a customized {@link PepperMapper} object and returns it. You can here do some additional initialisations. 
	 * Thinks like setting the {@link Identifier} of the {@link SDocument} or {@link SCorpus} object and the {@link URI} resource is done
	 * by the framework (or more in detail in method {@link #start()}).  
	 * The parameter <code>sElementId</code>, if a {@link PepperMapper} object should be created in case of the object to map is either 
	 * an {@link SDocument} object or an {@link SCorpus} object of the mapper should be initialized differently. 
	 * <br/>
	 * 
	 * @param sElementId {@link Identifier} of the {@link SCorpus} or {@link SDocument} to be processed. 
	 * @return {@link PepperMapper} object to do the mapping task for object connected to given {@link Identifier}
	 */
	public PepperMapper createPepperMapper(Identifier sElementId){
		TCFMapperImport mapper=new TCFMapperImport();
		mapper.setResourceURI(getIdentifier2ResourceTable().get(sElementId));
		return(mapper);
	}

// =================================================== optional ===================================================	
	/**
	 * <strong>OVERRIDE THIS METHOD FOR CUSTOMIZATION</strong>
	 * 
	 * This method is called by the pepper framework after initializing this object and directly before start processing. 
	 * Initializing means setting properties {@link PepperModuleProperties}, setting temprorary files, resources etc. .
	 * returns false or throws an exception in case of {@link PepperModule} instance is not ready for any reason.
	 * @return false, {@link PepperModule} instance is not ready for any reason, true, else.
	 */
	@Override
	public boolean isReadyToStart() throws PepperModuleNotReadyException
	{
		//TODO make some initializations if necessary
		return(super.isReadyToStart());
	}
}
