function getAnswer(answer){
    if(typeof(specialOfftopic) != 'undefined'){
        return specialOfftopic[answer];
    }
    return offtopic[answer]
}