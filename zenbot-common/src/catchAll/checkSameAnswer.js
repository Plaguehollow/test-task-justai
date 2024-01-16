(function() {
    
    function checkSameAnswer($context) {
        var session = $context.session;
        var response = $context.response;
        session.catchAll = session.catchAll || {};

        //проверяем, не собирается ли бот выдать тот же ответ, что в прошлый раз
        var repetition = false;

        if (session.lastAnswer && session.lastAnswer == response.answer) {
            repetition = true;
        }
        session.lastAnswer = response.answer;

        if (repetition) {
            var context = $context.contextPath;
            if (!context.startsWith("/CatchAll") && !context.endsWith('CatchAll')) {
                $reactions.transition("/CatchAll");
            }
        }
    }

    bind("postProcess", checkSameAnswer);

})();
