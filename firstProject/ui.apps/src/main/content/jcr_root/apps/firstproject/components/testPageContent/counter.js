// Este java script sirve para sumarle 1 al index de una lista.
// En este ejemplo a las lista de nodos hijos.

use( function () {
	'use strict';
	// via this. you can refer to parameters.
	var currentIndex = this.index;
	
	log.info("Currentindex: " + currentIndex);
	
	return {
		incrementedIndex: ++currentIndex
	};	
});
