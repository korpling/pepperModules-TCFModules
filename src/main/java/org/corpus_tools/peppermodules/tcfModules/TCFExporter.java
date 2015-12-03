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

import org.corpus_tools.pepper.impl.PepperExporterImpl;
import org.corpus_tools.pepper.modules.PepperExporter;
import org.corpus_tools.pepper.modules.PepperMapper;
import org.corpus_tools.salt.common.SDocument;
import org.corpus_tools.salt.graph.Identifier;
import org.eclipse.emf.common.util.URI;
import org.osgi.service.component.annotations.Component;

@Component(name = "TCFExporterComponent", factory = "PepperExporterComponentFactory")
public class TCFExporter extends PepperExporterImpl implements PepperExporter {
	public TCFExporter() {
		super();
		this.setExportMode(EXPORT_MODE.DOCUMENTS_IN_FILES);
		this.setName("TCFExporter");
		setSupplierContact(URI.createURI("saltnpepper@lists.hu-berlin.de"));
		setSupplierHomepage(URI.createURI("https://github.com/korpling/pepperModules-TCFModules"));
		setDesc("This exporter transforms a Salt model into the TCF format produced for instance by WebLicht (see http://weblicht.sfs.uni-tuebingen.de/) or WebAnno (see https://www.ukp.tu-darmstadt.de/software/webanno/). ");
		this.addSupportedFormat("TCF", "0.4", null);
		this.setProperties(new TCFExporterProperties());
	}

	@Override
	public PepperMapper createPepperMapper(Identifier sElementId) {
		TCFMapperExport mapper = new TCFMapperExport();
		if (sElementId.getIdentifiableElement() instanceof SDocument) {
			mapper.setResourceURI(getIdentifier2ResourceTable().get(sElementId));
		}
		return mapper;
	}
}
