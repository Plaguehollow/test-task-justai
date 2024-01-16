require: ../number/number.sc
require: floatNumberConverter.js

patterns:
    $FloatNumberFraction = ((десяты*|десятки):10 |
        (соты*|сотки):100 |
        (тысяч*):1000 |
        (десят* тысяч*|десятитысяч*):10000)

    $fractionWithDelimeter = $regexp<\d+\/10+>
    $floatNumberWithDelimeter = $regexp<\d+[.,]\d+> || converter = $converters.floatNumberWithDelimeterConverter
    $floatNumberWordsDelimeted = $Number::IntegerNumber (цел*|запятая|и) [и] ($Number::FractionalNumber [$FloatNumberFraction]|$fractionWithDelimeter) || converter = $converters.floatNumberWordsDelimetedConverter
    
    $specialFloatNumber = ($specialNumber|$piNumber)
    $specialNumber = (полтора:1|(~прямой ~угол):2|(~половина ~прямой ~угол):7|половина:8|четверть:9|треть:10) || converter = $converters.specialFloatNumberConverter
    $piNumber = ([числ*] [$Number|$NumberDigit::Number] пи:3|
                [число*] пи [на] (пополам|два|2):4|
                [число*] (три|3) пи [на] (пополам|два|2):6|
                [число*] пи [на] (четыре|4):11|
                [число*] пи [на] (шесть|6):12) || converter = $converters.specialFloatNumberConverter

    $Denominator = ((вторых|вторая):2|
        (третьих|трети|треть):3|
        (четвертых|четверти|четвертая):4|
        (пятых|пятая):5|
        (шестых|шестая):6|
        (седьмых|седьмая):7|
        (восьмых|восьмая):8|
        (девятых|девятая):9|
        (десятых|десятая):10)
    $Fraction = $Number $Denominator || converter = $converters.fractionConverter

    $FloatNumber = ($floatNumberWordsDelimeted|$floatNumberWithDelimeter|$specialFloatNumber|$Fraction) || converter = $converters.propagateConverter

    $CalcNumber = ($Number|$FloatNumber) || converter = $converters.propagateConverter
    $CalcQuestion = [это] (будет [ли] [равн*]|равн* [ли]|это|=) $CalcNumber

    $combinedExpressionWithoutSpaces = $regexp<(ноль|один|два|три|четыре|пять|шесть|семь|восемь|девять|десять|одиннадцать|двенадцать|тринадцать|четырнадцать|пятнадцать|шестнадцать|семнадцать|восемнадцать|девятнадцать|двадцать)(\+|-|\*|×|÷|\/)\d+> || converter = $converters.combinedExpressionWithoutSpacesConverter
    $digitWithOperator = $regexp<((\+|-)\d+)+>
    $SimpleExpressionWithoutSpaces = $regexp<\d+((\+|-|\*|×|÷|\/)\d+)+>
    $divider = $regexp<(÷|\/)>
    $star = $regexp<(\*|×)>  
    $CalcNegOp = (минус|-)
    $CalcMul = (дважды:2 | трижды:3 | четырежды:4 | пятью:5 | шестью:6 | семью:7 | восемью:8 | девятью:9)
    $CalcPower = (([возведи*] [в] (квадрате|квадрат)):2|([возведи*] [в] (куб|кубе)):3|[возв*] [в|во] {степен* [$CalcOp] $CalcNumber})
    $CalcOp = ((плюс*|прибав*|добав*|сложи с|+):+|((минус|отнять|отними*|вычесть|вычита*|вычти|отнимай|-) [от|из]):-|((*множ*|$star) [на]):mul|((*дели*|дели) [на]|$divider):del)
    $CalcOpPrefix = ((~сумма):+|(~разность/~разница):-|(~произведение):mul/(частное):del)
    $CalcFunc = (({[квадрат*] ~корень} [из|от]):Sqrt |([натуральн*] логарифм* [от]):Ln|(десятичн* логарифм* [от]):Log|({кубич* кор*} [из|от]):Cbrt|(~корень {$CalcNumber степени} [из|от]):rt)
    $CalcPercent = $CalcNumber (процент*|%) [от|из]
    $CalcSin = синус* [для|от] [угл* [в]]
    $CalcCos = косинус* [для|от] [угл* [в]]
    $CalcTan = тангенс* [для|от] [угл* [в]]
    $CalcCtg = котангенс* [для|от] [угл* [в]]
    $CalcRSin = синус* [для|от] [угл* [в]] [$CalcNegOp] 
    $CalcRCos = косинус* [для|от] [угл* [в]] [$CalcNegOp] 
    $CalcRTan = тангенс* [для|от] [угл* [в]] [$CalcNegOp] 
    $CalcRCtg = котангенс* [для|от] [угл* [в]] [$CalcNegOp] 

    $SimpleExpression = ($CalcOp [$CalcNumber]|[$CalcNumber] $CalcPower|$digitWithOperator|($CalcOpPrefix [между] $CalcNumber::Number1 и $CalcNumber::Number2)) || converter = $converters.mathSimpleExpressionConverter

    $MathExpression = ($specialMathExpression|$SimpleExpression|$CalcFunc [$CalcNumber]|$CalcPercent [$CalcNumber]|$CalcMul [$CalcNumber]|($CalcSin|$CalcCos|$CalcTan|$CalcCtg) $CalcNumber [градус*] | ($CalcRSin|$CalcRCos|$CalcRTan) ($CalcNumber радиан*|$piNumber [радиан*])|$CalcRCtg ($CalcNumber радиан*|$piNumber [радиан*])) || converter = $converters.mathExpressionConverter

    $mathMinus1 = (вычти|вычесть|отними)
    $mathMinus2 = (вычти|вычесть|отними)
    $mathMinus3 = минут
    $mathPlus = (сложи|сложить)
    $mathAdd = (прибавь|прибавить)
    $mathAdd2 = и
    $mathDiv = (*делить|дели|раздели|подели)
    $mathMul1 = (помножь|перемножь|умножь|умножить|перемножить|помножить)
    $mathMul2 = (на|раз* по)
    $mathMul3 = (от)
    $specialMathExpression = ($mathMinus1 $CalcNumber (из|от) $CalcNumber|
        $mathMinus2 (из|от) $CalcNumber $CalcNumber|
        $CalcNumber $mathMinus3::mathMinus2 $CalcNumber|
        $mathPlus $CalcNumber (и|с) $CalcNumber|
        $mathAdd {к $CalcNumber} $CalcNumber|
        $CalcNumber $mathAdd2::mathAdd $CalcNumber|
        $mathMul1 $CalcNumber (и|на) $CalcNumber|
        $mathDiv $CalcNumber на $CalcNumber|
        $CalcNumber $mathMul2 [$CalcNegOp] $CalcNumber|
        $SimpleExpressionWithoutSpaces|
        $combinedExpressionWithoutSpaces::SimpleExpressionWithoutSpaces|
        $CalcNumber $mathMul3 $CalcNumber) || converter = $converters.specialMathExpressionConverter