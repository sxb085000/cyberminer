var LineStorage = function () {
	this.lines = [];
};

LineStorage.prototype.getLines = function(){
	return this.lines;
};

LineStorage.prototype.setLines = function(lines){
	this.lines = lines;
};