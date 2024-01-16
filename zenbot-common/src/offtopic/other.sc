theme: /Offtopic

    state: WhatCanYouDo
        q!: [$AnyWord] [$AnyWord] (меню|помощь|справка) [$AnyWord] [$AnyWord]
        q!: * {как* вопрос* * [я] мо* [$you] зада*} *
        q!: * {$what * [я] мо* спрос* * $you} *
        q!: * {$what * $you * мо* расска*} *
        q!: * {$what * $you * умеешь *} *
        a: {{getAnswer('WhatCanYouDo')}}

    state: YouMisunderstoodMe
        q!: * $you * [меня] * (не|неправильно) (понимае*|понял*|поняли) * 
        q!: * {(недовол*|не удовлетвор*|не понравил*) [$AnyWord] [$AnyWord] (бесед*|диалог*|общение*)} *
        a:  {{getAnswer('YouMisunderstoodMe')}}

    state: Test || noContext = true
        q!: (тест|прием|есть контакт|проверка связи) [прием]
        a: Связь в порядке.
        go!: /Offtopic/HowCanIHelpYou?

    state: MyNameIs || noContext = true
        q!: {[$AnyWord] [$AnyWord] меня зовут [$AnyWord] [$AnyWord]}
        q!: * {[$AnyWord] [$AnyWord] $my (имя|прозвище|кличка|ник|никнейм|ник-нейм|ник нейм) [$AnyWord] [$AnyWord] }
        a: Очень приятно.
        go!: /Offtopic/HowCanIHelpYou?


    state: WaitAMoment || noContext = true
        q!: {(*ждите|подожд*) [одну|пару] * (минут*|секунд*|немного|немнож*)}
        q!: {(одну|пару) * (минут*|секунд*)}
        q!: (минут*|секунд*)
        q!:  не (отключай*|переключай*|переключай*|отсодиняй*) 
        a: Я подожду, не торопитесь.

