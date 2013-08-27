package rundeck

class ExecReport extends BaseReport{

    String execId
    String jobId
    String jobFullName
    Boolean adhocExecution
    String adhocScript
    String abortedByUser

    static mapping = {
        adhocScript type: 'text'
    }

    static constraints = {
        adhocExecution(nullable:true)
        execId(nullable:true,blank:true)
        jobId(nullable:true,blank:true)
        jobFullName(nullable:true,blank:true)
        adhocScript(nullable:true,blank:true)
        abortedByUser(nullable:true,blank:true)
    }

    public static final ArrayList<String> exportProps = BaseReport.exportProps +[
            'execId',
            'jobId',
            'jobFullName',
            'adhocExecution',
            'adhocScript',
            'abortedByUser'
    ]
    def Map toMap(){
        this.properties.subMap(exportProps)
    }

    static buildFromMap(ExecReport obj, Map map) {
        BaseReport.buildFromMap(obj, map)
    }
    /**
     * Generate an ExecReport based off of an existing execution
     * @param exec
     * @return
     */
    static ExecReport fromExec(Execution exec){
        def failedCount = exec.failedNodeList ?exec.failedNodeList.split(',').size():0
        def successCount=exec.failedNodeList?0:1;
        def totalCount = exec.failedNodeList ? failedCount : 1;
        def adhocScript = (null == exec.scheduledExecution) ? exec.workflow.commands[0].adhocRemoteString : null
        def summary = "[${exec.workflow.commands.size()} steps]"
        def issuccess = exec.status == 'true'
        def iscancelled = exec.cancelled
        return fromMap([
                execId:exec.id,
                jobId: exec.scheduledExecution?.extid,
                adhocExecution: null==exec.scheduledExecution,
                adhocScript: adhocScript,
                abortedByUser: iscancelled? exec.abortedby ?: exec.user:null,
                node:"${failedCount}/${successCount}/${totalCount}",
                title: adhocScript?adhocScript:summary,
                status: issuccess ? "succeed" : iscancelled ? "cancel" : "fail",
                ctxProject: exec.project,
                jobFullName: exec.scheduledExecution?( exec.scheduledExecution.groupPath ? exec.scheduledExecution.generateFullName() : exec.scheduledExecution.jobName): 'adhoc',
                author: exec.user,
                message: (issuccess ? 'Job completed successfully' : iscancelled ? ('Job killed by: ' + (exec.abortedby ?: exec.user)) : 'Job failed'),
                dateStarted: exec.dateStarted,
                dateCompleted: exec.dateCompleted,
                actionType: issuccess ? "succeed" : iscancelled ? "cancel" : "fail"
        ])
    }
    static ExecReport fromMap(Map map) {
        def report = new ExecReport()
        buildFromMap(report, map.subMap( exportProps))
        report
    }
}
