require: text/text.sc
require: number/number.sc
require: dateTime/dateTime.sc
require: address/address.sc

require: city/cities-small.csv
    name = Cities
    var = $Cities
#TODO: pass dictionary name through $injector
require: city/city.sc

require: newSessionOnStart/newSession.sc
    injector = {
        newSessionStartState: "/NewSessionWelcome",
        newSessionTimeout: 10
        }

require: catchAll/catchAll.sc


theme: /

#    state: Text
#        q: $Text
#        a: text: {{ $parseTree._Text }}

#    state: NoMatch
#        q: *
#        a: NoMatch

    state: NumberPattern
        q: * $Number *
        a: number: {{$parseTree._Number}}

    state: DateTimePattern
        q: * $DateTime *
        a: dateTime: {{ toPrettyString( $parseTree._DateTime ) }}
#        a: dateTime: {{ toPrettyString( $converters.absoluteDateTimeConverter($parseTree._DateTime) )}}

    state: CityPattern
        q: * $City *
        if: $parseTree._City
            a: city: {{$parseTree._City.name}}
        else:
            a: 111

    q: тест на создание новой сессии || toState = /Start
    state: NewSessionWelcome
        a: welcome

        state:
            q: context pattern
            a: context

