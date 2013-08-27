package rundeck

class BaseReport {

    String node
    String title
    String status
    String actionType
    String ctxProject
    String tags
    String author
    Date dateStarted
    Date dateCompleted 
    String message

    static mapping = {
        message type: 'text'
        title type: 'text'
    }
   static constraints = {
        tags(nullable:true)
        node(nullable:true)
        status(nullable:false,inList:['succeed','fail','cancel'])
        actionType(nullable:false,inList:['create','update','delete','succeed','fail','cancel'])
    }
    public static final ArrayList<String> exportProps = [
            'node',
            'title',
            'status',
            'actionType',
            'ctxProject',
            'tags',
            'author',
            'message',
            'dateStarted',
            'dateCompleted'
    ]

    def Map toMap(){
        this.properties.subMap(exportProps)
    }

    static buildFromMap(BaseReport obj, Map data) {
        data.each {k, v ->
            obj[k] = v
        }
    }

    static BaseReport fromMap(Map data) {
        def BaseReport report = new BaseReport()
        buildFromMap(report, data.subMap(exportProps))
        report
    }
}
