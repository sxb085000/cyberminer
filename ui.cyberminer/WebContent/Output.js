var Output = function (cs, as) {
	this.cs = cs.getLines();
	this.as = as.getLines();
};

Output.prototype.read = function() {

	$("#outputContent").empty();
	  
	var outputLine = $("<div class='output-line'></div>");
	
	var csDiv = $("<div class='output-left ui-widget-content ui-corner-all'></div>");
	csDiv.append("<h2>Circular Shift<img class='cs-help' src='stylesheets/images/help-icon.png' title='Repeatedly removes the first word of the line and append it at the end of the line until it reaches the original input.'></h2>");
	  
	var textArea = $("<textarea rows='12' cols='50' readonly='readonly' style='resize:none;'></textarea>");
	for(var i=0; i < this.cs.length; i++) {
		textArea.append("CS"+i+": "+this.cs[i]+"\n");
	}
	csDiv.append(textArea);
	  
	outputLine.append(csDiv);
	  
	var asDiv = $("<div class='output-right ui-widget-content ui-corner-all'></div>");
	asDiv.append("<h2>Alphabetic Shift<img class='as-help' src='stylesheets/images/help-icon.png' title='Sorts the circular shift output in an ascending alphabetical order.'></h2>");
	  
	textArea = $("<textarea rows='12' cols='50' readonly='readonly' style='resize:none;'></textarea>");
	for(var i=0; i < this.as.length; i++) {
		textArea.append("AS"+i+": "+this.as[i]+"\n");
	}
	asDiv.append(textArea);
	  
	outputLine.append(asDiv);
	  
	$("#outputContent").append(outputLine);
};