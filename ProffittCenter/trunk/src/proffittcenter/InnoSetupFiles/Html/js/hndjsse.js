var HndJsSe=function(){this.sInput="";this.nPos=-1;this.sWord=this.sChar="";this.sMode="OR";this.bNewWord=this.bFirstWord=!0;this.bInQuote=!1;this.aQuery=[];this.aResults=[]}; HndJsSe.prototype={AddWord:function(){if(this.sWord!=""){if(this.bFirstWord)this.sMode="OR",this.bFirstWord=!1;this.aQuery.push([this.sWord.toLowerCase(),this.sMode]);this.sWord="";this.bNewWord=!0;this.bInQuote=!1}},PeakChar:function(a){a||(a=1);return this.sInput[this.nPos+a]?this.sInput[this.nPos+a]:""},NextChar:function(a){a||(a=1);var b=this.PeakChar(a);b!=""&&(this.nPos+=a);return b},ParseInput:function(a){this.nPos=-1;this.sWord=this.sChar="";this.sMode="OR";this.bNewWord=this.bFirstWord=!0; this.bInQuote=!1;this.aQuery=[];this.sInput=a;for(this.sChar=this.NextChar();this.sChar!="";)this.sChar==" "||this.sChar=="\t"||this.sChar=="\u000b"||this.sChar=="\u000c"||this.sChar=="\u00a0"||this.sChar=="\ufeff"||this.sChar=="\n"||this.sChar=="\r"||this.sChar=="\u2028"||this.sChar=="\u2029"||this.sChar==" "||this.sChar=="\u00a0"||this.sChar=="\u1680"||this.sChar=="\u180e"||this.sChar=="\u2000"||this.sChar=="\u2001"||this.sChar=="\u2002"||this.sChar=="\u2003"||this.sChar=="\u2004"||this.sChar== "\u2005"||this.sChar=="\u2006"||this.sChar=="\u2007"||this.sChar=="\u2008"||this.sChar=="\u2009"||this.sChar=="\u200a"||this.sChar=="\u202f"||this.sChar=="\u205f"||this.sChar=="\u3000"?this.bInQuote?this.sWord+=this.sChar:this.PeakChar(1)=="O"&&this.PeakChar(2)=="R"?(this.AddWord(),this.sMode="OR",this.NextChar(3)):this.PeakChar(1)=="N"&&this.PeakChar(2)=="O"&&this.PeakChar(3)=="T"?(this.AddWord(),this.sMode="NOT",this.NextChar(4)):this.PeakChar(1)=="A"&&this.PeakChar(2)=="N"&&this.PeakChar(3)=="D"? (this.AddWord(),this.sMode="AND",this.NextChar(4)):(this.AddWord(),this.sMode="AND"):this.sChar=='"'?this.bInQuote?this.AddWord():this.bInQuote=!0:this.sWord+=this.sChar,this.sChar=this.NextChar();this.AddWord()},AddResultTopics:function(a){if(a&&a.length)for(var b=0;b<a.length;b++)this.aResults.indexOf(a[b][0])<0&&this.aResults.push(a[b][0])},RemoveResultTopics:function(a){if(a&&a.length)for(var b,c=a.length-1;c>=0;c--)b=this.aResults.indexOf(a[c][0]),b>=0&&this.aResults.splice(b,1)},IntersectResultTopics:function(a){if(a&& a.length)for(var b=!1,c=this.aResults.length-1;c>=0;c--){for(var b=!1,d=0;d<a.length;d++)if(this.aResults[c]==a[d][0]){b=!0;break}b||this.aResults.splice(c,1)}},PerformSearch:function(){this.aResults=[];var a;for(a=0;a<this.aQuery.length;a++)this.aQuery[a][1]=="OR"&&oWl[this.aQuery[a][0]]?this.AddResultTopics(oWl[this.aQuery[a][0]]):this.aQuery[a][1]=="NOT"?this.RemoveResultTopics(oWl[this.aQuery[a][0]]):this.aQuery[a][1]=="AND"&&this.IntersectResultTopics(oWl[this.aQuery[a][0]])}};