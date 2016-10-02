var plugin = null;
function Plugin(obj) {
    if (plugin != null) {
        throw 'Multiple plugins in one file is not supported.';
    }
    var AbstractPlugin = Java.type("jp.uphy.servermonitor.plugin.api.AbstractPlugin");
    var MyPlugin = Java.extend(AbstractPlugin, obj);
    plugin = new MyPlugin();
}
function task(f) {
    var MyTask = Java.extend(Java.type('jp.uphy.servermonitor.plugin.api.ScheduleTask'), {
        run: function () {
            f();
        }
    });
    return new MyTask();
}
var console = {
    log: function (o) {
        java.lang.System.out.println(o);
    }
};