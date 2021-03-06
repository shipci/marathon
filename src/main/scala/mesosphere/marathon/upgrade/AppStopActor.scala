package mesosphere.marathon.upgrade

import akka.event.EventStream
import mesosphere.marathon.SchedulerActions
import mesosphere.marathon.event.MesosStatusUpdateEvent
import mesosphere.marathon.state.AppDefinition
import mesosphere.marathon.tasks.TaskTracker
import org.apache.mesos.SchedulerDriver

import scala.collection.mutable
import scala.concurrent.Promise

class AppStopActor(
    driver: SchedulerDriver,
    scheduler: SchedulerActions,
    val taskTracker: TaskTracker,
    val eventBus: EventStream,
    app: AppDefinition,
    val promise: Promise[Unit]) extends StoppingBehavior {

  var idsToKill = taskTracker.get(app.id).map(_.getId).to[mutable.Set]
  def appId = app.id

  def initializeStop(): Unit = {
    eventBus.subscribe(self, classOf[MesosStatusUpdateEvent])
    scheduler.stopApp(driver, app)
  }
}
