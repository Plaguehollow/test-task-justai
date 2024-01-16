require: ../city/city.sc
require: ../city/cities-ru.csv
  name = Cities
  var = $Cities
require: whereConverter.js
require: countries-ru.csv
  name = Countries
  var = $Countries
require: geography-ru.csv
  name = Geography
  var = $Geography
  
init:
    if (!$global.$converters) {
        $global.$converters = {};
    }

patterns:
    $Where = ($City|$Capital|$Country2City) || converter = $converters.propagateConverter
    $Capital = $entity<Geography> || converter = $converters.geographyConverter   
    $Country = $entity<Countries> || converter = $converters.countryConverter
    $Country2City = $entity<Countries> || converter = $converters.country2CapitalConverter

theme: /Errors

    state: Unknown location || noContext = true
        a: Не могу найти координаты для страны {{$parseTree.Where[0].value.country}}. Обычно я могу найти координаты, если знаю название нужного города.