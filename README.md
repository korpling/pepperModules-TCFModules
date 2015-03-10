pepperModules-TCFModules
==================
### General information
TCFModules is part of the [SaltNPepper](https://korpling.german.hu-berlin.de/p/projects/saltnpepper/) project. This Pepper module allows the conversion of data provided in the [TCF format](http://weblicht.sfs.uni-tuebingen.de/weblichtwiki/index.php/The_TCF_Format) to Salt, a graph-based data model. TCF is a common XML data exchange format which has been developed within the [WebLicht](http://weblicht.sfs.uni-tuebingen.de/weblichtwiki/) architecture.
This module has been developed in cooperation of the [Clarin-D](http://de.clarin.eu/) center Universität Stuttgart and the Humboldt-Universität zu Berlin.  
[[read more]](http://korpling.github.io/pepperModules-TCFModules)
  
  
License
-------
Copyright 2014 Clarin-D, Humboldt-Universität zu Berlin.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Requirements
-------
Since the here provided module is a plugin for Pepper, you need an instance of the Pepper framework. If you do not already have a running Pepper instance, click on the link below and download the latest stable version (not a SNAPSHOT):
[SaltNPepper](http://korpling.german.hu-berlin.de/saltnpepper/repository/saltNpepper_full/)

Install TCFModules
-------
If this Pepper module is not yet contained in your Pepper distribution, you can easily install it. Just open a command line and start pepper.

**Windows**
```
pepperStart.bat 
```

**Linux/Unix**
```
bash pepperStart.sh 
```

Pepper will start in command line mode (the command line starts with `pepper>`). To install this module, type:
```
update pepperModules-TCFModules
``` 
If your Pepper instance is configured for TCFModules, the update process will start. If not, you will read something like this:
```
pepperModules-TCFModules is not a known module.
``` 
To use pepperModules-TCFModules nevertheless, you can start the installation by adding an entry to Pepper's update list with the following command:
```
update de.hu_berlin.german.korpling.saltnpepper::pepperModules-TCFModules::https://korpling.german.hu-berlin.de/maven2 
``` 
Note, that the update is performed directly after typing that. From then on you will be able to start updates of pepperModules-TCFModules with `update pepperModules-TCFModules`.

## Usage
To use this module in your Pepper workflow, put the following lines into the workflow description file. Note the fixed order of xml elements in the workflow description file: &lt;importer/&gt, &lt;manipulator/&gt, &lt;exporter/&gt.
A detailed description of the Pepper workflow can be found on the [Pepper project site](https://github.com/korpling/pepper). 

### a) Identify the module by name

```xml
<importer name="TCFImporter" path="PATH_TO_CORPUS"/>
``` 
or
```xml
<exporter name="TCFExporter" path="PATH_TO_CORPUS"/>
``` 

### c) Use properties
```xml
<importer name="TCFImporter" path="PATH_TO_CORPUS">
  <customization>
    <property key="PROPERTY-1_NAME">PROPERTY-1_VALUE</key>
    <property key="PROPERTY-2_NAME">PROPERTY-2_VALUE</key>
    ...
  </customization>
</importer>
``` 

## Contribute
Since this Pepper module is under a free license, please feel free to fork it from github and improve the module. If you even think that others can benefit from your improvements, don't hesitate to make a pull request, so that your changes can be merged.
If you have found any bugs, or have some feature request, please open an issue on github. If you need any help, please write an e-mail to saltnpepper@lists.hu-berlin.de.

## Funders
This project was funded by the [Clarin-D project](http://www.clarin-d.de/) and realized at the [department of corpus linguistics and morphology](http://www.linguistik.hu-berlin.de/institut/professuren/korpuslinguistik/) of the Humboldt Universität.

# TCFImporter

## Mapping to Salt

The importer maps each TCF layer to an SLayer object in Salt. Attributes refering to the
TCF layer are stored as meta annotations on the SLayer itself without using a
namespace. All annotations contained in the TCF layer are imported as annotations on
SToken and/or SSpan objects where the annotation's namespace is (in most cases, cf.
table below) the SLayer's name and the annotation's name is the tag's name without
the xml namespace or the attributes name. E.g.:
```xml
&lt;constituent cat="VVFIN" ID="c_1" tokenIDs="t_1"/>
``` 
In this case the token is annotated with syntax::cat="VVFIN" in the salt model. An overview of all TCF-Layers and according SLayers can be found in the following table:
|TCF layer|namespace of annotation|node annotation names|edge annotation names|meta annotations on SLayer|
|---------|-----------------------|---------------------|---------------------|--------------------------|



# TCFExporter
