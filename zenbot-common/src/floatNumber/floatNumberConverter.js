$global.$converters = $global.$converters || {};
var cnv = $global.$converters;

cnv.floatNumberWithDelimeterConverter = function(parseTree) {
    return parseFloat(parseTree.text.replace(",", "."));
}

cnv.floatNumberWordsDelimetedConverter = function(parseTree) {
    var res, fraction;
    if (parseTree.fractionWithDelimeter) {
       fraction = eval(parseTree.fractionWithDelimeter[0].text);  
    } else {
        if (parseTree.FloatNumberFraction) {
            fraction = parseTree.FractionalNumber[0].value / parseTree.FloatNumberFraction[0].value;
        } else {
            fraction = parseTree.FractionalNumber[0].value / 10;
            if (parseTree.nought) {
                for (var i = 0; i < parseTree.nought.length; i++) {
                    fraction = parseTree.FractionalNumber[0].value / 10;
                }       
            }
            while (fraction >= 1) {
                fraction = fraction / 10;
            }
        }
    }
    res = parseFloat(parseTree.IntegerNumber[0].value) + parseFloat(fraction); 
    return res;        
}

cnv.specialFloatNumberConverter = function(parseTree) {
    switch (parseTree.value) {
        case "1":
            return 1.5;
        case "2":
            return 90;
        case "3":
            var num = 1;
            if (parseTree.Number) {
                num = parseInt(parseTree.Number[0].value)
            }
            return num * Math.PI;    
        case "4":
            return Math.PI/2;
        case "5":    
            return parseTree.text.split('/')[0] / parseTree.text.split('/')[1]; 
        case "6":
            return 3 * Math.PI/2;
        case "7":
            return 45; 
        case "8":
            return 0.5; 
        case "9":
            return 0.25;
        case "10":
            return (1/3); 
        case "11":
            return Math.PI/4; 
        case "12":
            return Math.PI/6;  

    }        
}

cnv.fractionConverter = function(parseTree) {
    var res = parseTree.Number[0].value / parseTree.Denominator[0].value;
    res = +(Math.round(res + "e+2")  + "e-2");
    return res;
}

cnv.mathExpressionConverter = function(parseTree) {
    var operation = '';
    var operand = '';
    var rt = '';
    if (parseTree.SimpleExpression) {
        return cnv.mathSimpleExpressionConverter(parseTree.SimpleExpression[0]);
    } 
    if (parseTree.specialMathExpression) {
        return cnv.specialMathExpressionConverter(parseTree.specialMathExpression[0]);
    }     
    if (parseTree.CalcNumber) {
        operand = parseTree.CalcNumber[0].value;
    }
    if (parseTree.CalcFunc) {
        switch(parseTree.CalcFunc[0].value) {
            case "Sqrt":
                operation = "Math.sqrt";
                break;
            case "Log":
                operation = "Math.log10";
                break;
            case "Ln":
                operation = "Math.log";
                break;
            case "Cbrt":
                operation = "Math.pow";
                rt = "1/3";
                break;
            case "rt":
                operation = "Math.pow";
                rt =  "1/" + parseTree.CalcFunc[0].CalcNumber[0].value;
                break;
        }
        return operation + "(" + (operand == '' ? "next" : operand) + (operation == "Math.pow" ? ", " + rt : "") + ")";
    }
    if (parseTree.CalcPercent) {
        var percent = parseTree.CalcPercent[0].CalcNumber[0].value;     
        return "CalcPercent(" + percent + "," + (operand == '' ? "next" : operand) + ")";
    }
    if (parseTree.CalcMul) {
        return parseTree.CalcMul[0].value + "*" + (operand == '' ? "next" : operand);
    }
    if (parseTree.CalcSin) {
        operand = parseTree.CalcNumber[0].value;
        return "Math.sin(" + operand + "* (Math.PI/180))";
    }
    if (parseTree.CalcCos) {
        operand = parseTree.CalcNumber[0].value;      
        return "Math.cos(" + operand + "* (Math.PI/180))";
    }    
    if (parseTree.CalcTan) {
        operand = parseTree.CalcNumber[0].value;                    
        return "Math.tan(" + operand + "* (Math.PI/180))";
    }  
    if (parseTree.CalcCtg) {
        operand = parseTree.CalcNumber[0].value;                    
        return "1.0/Math.tan(" + operand + "* (Math.PI/180))";
    }
    if (parseTree.CalcRSin) {
        if (parseTree.CalcNumber) {
            operand = parseTree.CalcNumber[0].value;               
            return "Math.sin(" + operand + ")";  
        } 
        else {
            if (parseTree.CalcNegOp) {
                operand = '-' + parseTree.piNumber[0].value; 
            }
            else {
                operand = parseTree.piNumber[0].value;
            }
            return "Math.sin(" + operand + ")";  
        }     
    }
    if (parseTree.CalcRCos) {
        if (parseTree.CalcNumber) {
            operand = parseTree.CalcNumber[0].value;                   
            return "Math.cos(" + operand + ")";
        } 
        else {
            if (parseTree.CalcNegOp) {
                operand = '-' + parseTree.piNumber[0].value; 
            }
            else {
                operand = parseTree.piNumber[0].value;
            }               
            return "Math.cos(" + operand + ")";  
        }
    }    
    if (parseTree.CalcRTan) {
        if (parseTree.CalcNumber) {
            operand = parseTree.CalcNumber[0].value;                    
            return "Math.tan(" + operand + ")";
        } 
        else {
            if (parseTree.CalcNegOp) {
                operand = '-' + parseTree.piNumber[0].value; 
            }
            else {
                operand = parseTree.piNumber[0].value;
            }                   
            return "Math.tan(" + operand + ")";
        }
    } 
    if (parseTree.CalcRCtg) {
        if (parseTree.CalcNumber) {
            operand = parseTree.CalcNumber[0].value;                    
            return "1.0/Math.tan(" + operand + ")";
        } 
        else {
            if (parseTree.CalcNegOp) {
                operand = '-' + parseTree.piNumber[0].value; 
            }
            else {
                operand = parseTree.piNumber[0].value;
            }                    
            return "1.0/Math.tan(" + operand + ")";
        }
    } 
}

cnv.mathSimpleExpressionConverter = function(parseTree) {
    var operation = '';
    var operand = '';
    if (parseTree.CalcNumber) {
        operand = parseTree.CalcNumber[0].value;          
    }
    if (parseTree.CalcOp) {
        if (parseTree.CalcOp[0].value == "mul") {
            operation = "*";
        } else if (parseTree.CalcOp[0].value == "del") {
            operation = "/";
        } else {
            operation = parseTree.CalcOp[0].value;
        }
        return operation + operand;
    }
    if (parseTree.CalcPower) {
        var power;
        if (parseTree.CalcPower[0].CalcNumber) {
            power = parseTree.CalcPower[0].CalcNumber[0].value;
        } else {
            power = parseInt(parseTree.CalcPower[0].value);
        }
        return "Math.pow("+ (operand == '' ? "prev" : operand) + ","+ power +")";
    }
    if (parseTree.digitWithOperator) {
        return parseTree.text.replace(/÷/g,'/');
    }
    if (parseTree.CalcOpPrefix){
        if (parseTree.CalcOpPrefix[0].value == "mul") {
            operation = "*";
        } else if (parseTree.CalcOpPrefix[0].value == "del") {
            operation = "/";
        } else {
            operation = parseTree.CalcOpPrefix[0].value;
        }
        return (parseTree.Number1[0].value +operation + parseTree.Number2[0].value);
    }
}

cnv.specialMathExpressionConverter = function(parseTree) {
    if (parseTree.mathMinus1) {
        return parseTree.CalcNumber[1].value + '-' + parseTree.CalcNumber[0].value;
    }
    if (parseTree.mathMinus2) {
        return parseTree.CalcNumber[0].value + '-' + parseTree.CalcNumber[1].value;
    }    
    if (parseTree.mathPlus) {
        return parseTree.CalcNumber[0].value + '+' + parseTree.CalcNumber[1].value;
    }
    if (parseTree.mathAdd) {
        return parseTree.CalcNumber[0].value + '+' + parseTree.CalcNumber[1].value;
    }
    if (parseTree.mathMul1 || parseTree.mathMul2 || parseTree.mathMul3) {
        if (parseTree.CalcNegOp) {
            return parseTree.CalcNumber[0].value + '*-' + parseTree.CalcNumber[1].value;
        }
        else {
            return parseTree.CalcNumber[0].value + '*' + parseTree.CalcNumber[1].value;
        }
    }
    if (parseTree.mathDiv) {
        return parseTree.CalcNumber[0].value + '/' + parseTree.CalcNumber[1].value;
    }
    if (parseTree.SimpleExpressionWithoutSpaces) {
        if (parseTree.SimpleExpressionWithoutSpaces[0].value) {
            return parseTree.SimpleExpressionWithoutSpaces[0].value.replace(/÷/g,'/');
        } else {
            return parseTree.text.replace(/÷/g,'/');
        }
    } 
}

cnv.combinedExpressionWithoutSpacesConverter = function(parseTree) {
    var re = /(\+|-|\*|×|÷|\/)/;
    var parts = parseTree.text.split(re);
    return parseTree.text.replace(parts[0],word2value(parts[0]));
}

function word2value(word) {
    var result = $nlp.match(word, '/nlp', true);
    return cnv.propagateConverter(result.parseTree);
}