require: ../patterns.sc
require: emotionClassifier.sc
require: catchAllClassifier.js
require: checkSameAnswer.js

theme: /
    init:
        $global.catchAll = {
            dontKnowAnAnswerState: $injector.dontKnowAnAnswerState || "/CatchAll/Switch",
            livechatFinished: $injector.livechatFinished,
            closeChatPhrases: $injector.closeChatPhrases
        };

    state: CatchAll         || noContext = true
        q!: $catchAll

        script:
            // инициализируем переменные
            $session.catchAll = $session.catchAll || {};
            $session.catchAll.repetition = $session.catchAll.repetition || 0;

            // увеличиваем счётчик входов в catchAll
            $session.catchAll.repetition += 1;

            // определяем класс
            var clazz = catchAllClassifier.check($parseTree);

            // Если попадаем в catchAll в 3ий раз, то переводим на оператора
            // Бессмыслицу и ругань не переводим
             // if ($session.catchAll.repetition == 3 && clazz != "Nonsense" && clazz != "NegativeEmotion") {
                 // $reactions.transition(catchAll.dontKnowAnAnswerState);
             // } 
            // else if ($session.catchAll.repetition == 4 && clazz != "Nonsense" && clazz != "NegativeEmotion") {
                 // $reactions.transition("/CatchAll/AskAboutSwitch");
             // } else {
                // Иначе переходим в состояние, определённое классификатором catchAll'а
            $reactions.transition(clazz);
             // }


        state: Nonsense
            random:
                a: Простите, вы, кажется, опечатались.
                a: Похоже на опечатку...
            a: Конкретизируйте, пожалуйста.

        state: ProbablyNonsense
            if: $session.catchAll.repetition == 1
                random:
                    a: Простите, вы, кажется, опечатались. Конкретизируйте, пожалуйста.
                    a: Похоже на опечатку... Конкретизируйте, пожалуйста.
            elseif: $session.catchAll.repetition == 2
                a: Сформулируйте, пожалуйста, Ваш вопрос точнее.
            else:
                go!: /CatchAll/DoYouWannaSwitch?

        state: Transliteration
            a: Вы имели в виду '{{ $session.catchAll.transliterationText }}' ?

            state: Yes
                q: $agree       || onlyThisState = true
                go!: {{ $session.catchAll.transliterationState }}

            state: NoUnknown
                q: $disagree    || onlyThisState = true
                go!: /CatchAll/AskAgain?

        state: SeemsMeaningful
            if: $session.catchAll.repetition == 1
                random:
                    a: Простите, я робот и не понимаю некоторые фразы. Переформулируйте вопрос, пожалуйста.
                    a: Простите, мне не всегда удаётся понять смысл человеческой речи. Я робот. Сформулируйте свой вопрос иначе.
            else:
                go!: ../DoYouWannaSwitch?

        state: NegativeEmotion
            a: Пожалуйста, не сердитесь. Давайте вернемся к конструктивной беседе.


        # Кроме catchAll этот стейт используется в main.sc/postProcess для обработки Повторяющихся Ответов
        state: DoYouWannaSwitch?
            script:
                $session.nonsenseQuery = $parseTree.text;
            a: Хотите проконсультироваться по этому вопросу со специалистом?

            state: Yes
                q: * ($agree|переведи*|переводи*|перевести) *       || onlyThisState = true
                go!: /CatchAll/Switch

            state: No
                q: * ($disagree|не (переведи*|переводи*|перевести)) || onlyThisState = true
                go!: /CatchAll/AskAgain?
                
        state: DoYouWannaSwitch_Special
            script:
                $session.nonsenseQuery = $parseTree.text;
            a: Хотите проконсультироваться по этому вопросу со специалистом поддержки?

            state: Yes
                q: * ($agree|переведи*|переводи*|перевести) *       || onlyThisState = true
                go!: /CatchAll/SwitchSpecial

            state: No
                q: * ($disagree|не (переведи*|переводи*|перевести)) || onlyThisState = true
                go!: /CatchAll/AskAgain?
                
        state: Operator
            q!: * $serviceHelperHuman *
            q!: * {(соедин* [меня]|связать*|свяжи* [меня]|дозвонить*|позвонить|звонить|поговори*|переключ*|[мне] нужен|чат|[номер] для связи|связь*|телефон*) * [с|со|до|на] * $serviceHelperHuman} *
            q!: * департам* связи *
            q!: * не [с] (робот*|бот*|~автомат) *
            q!: * {(живог*|~живой) * (позов*|$need)} *
            q: * ($two|переключ* [на оператор*]|оператор*|$yes) *  || fromState = ../NotInCompetence
            script:
                $session.nonsenseQuery = $parseTree.text;
            go!: ../Switch

        state: Switch
            a: Перевожу на оператора. Если вы захотите снова побеседовать со мной, нажмите на [вернуться в беседу с роботом]
            #a: Перевожу на оператора.
            script:
                $client.history = $session.nonsenseQuery;
                $response.replies = $response.replies || [];
                $response.replies
                 .push({
                    type:"switch",
                    closeChatPhrases: catchAll.closeChatPhrases || ["/close"],
                    firstMessage: $client.history,
                    destination: "support",
                });

            state: NoOperatorsOnline
                event: noLivechatOperatorsOnline
                a: К сожалению, сейчас нет доступных операторов. Прошу Вас обратиться ко мне после 9 утра, я переключу Вас на оператора. Спасибо за понимание!
                    
                #state: GetUserInfo
                    #q: *
                    #script:
                        #var info = $parseTree.text;
                        #$response.replies = $response.replies || [];
                        #$response.replies
                         #.push({
                            #type:"switch",
                            #firstMessage: info,
                            #ignoreOffline: true,
                            #oneTimeMessage: true,
                            #destination: "support"
                         #});
                    #a: Спасибо, ваш вопрос отправлен оператору.

                #state: NoInfo
                    #q: (нет|не хочу|не буду) *
                    #a: Всего хорошего!
                    
        state: SwitchSpecial
            a: Перевожу на оператора. Если вы захотите снова побеседовать со мной, нажмите на [вернуться в беседу с роботом]
            #a: Перевожу на оператора.
            script:
                $client.history = $session.nonsenseQuery;
                $response.replies = $response.replies || [];
                $response.replies
                 .push({
                    type:"switch",
                    closeChatPhrases: catchAll.closeChatPhrases || ["/close"],
                    firstMessage: $client.history,
                    destination: "Aeroexpr",
                });

            state: NoOperatorsOnline
                event: noLivechatOperatorsOnline
                a: К сожалению, сейчас нет доступных операторов. Прошу Вас обратиться ко мне после 9 утра, я переключу Вас на оператора. Спасибо за понимание!

                #state: GetUserInfo
                    #q: *
                    #script:
                        #var info = $parseTree.text;
                        #$response.replies = $response.replies || [];
                        #$response.replies
                         #.push({
                            #type:"switch",
                            #firstMessage: info,
                            #ignoreOffline: true,
                            #oneTimeMessage: true,
                            #destination: "Aeroexpr"
                         #});
                    #a: Спасибо, ваш вопрос отправлен оператору.

                #state: NoInfo
                    #q: (нет|не хочу|не буду) *
                    #a: Всего хорошего!

        state: LivechatReset
            event: livechatFinished
            if: catchAll.livechatFinished
                go!: {{ catchAll.livechatFinished }}
            else:
                a: Диалог закрыт


        state: AskAgain?
            random:
                a: Что вы хотели узнать?
                a: Какой у вас вопрос?
                a: Если у Вас есть ещё какие-то вопросы, я к Вашим услугам.

        state: NotInCompetence
            a: Извините, данный вопрос не в моей компетенции. Перевести ваше обращение на оператора? Выберите нужный номер из списка:
                1. Продолжение диалога с электронным консультантом
                2. Переключение на оператора

            state: SwitchToHumanContinueConversation
                q: ($one|продолж*|1) *
                go!: /Offtopic/HowCanIHelpYou?