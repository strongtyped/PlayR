package twentysix.playr

import scala.annotation.tailrec

import RestRouteActionType.{Create, Custom, Delete, List, Read, RestRouteActionType, Update, Write}
import play.api.mvc.{EssentialAction, Handler, RequestHeader}

case class RouteFilterContext[T](path: String, sid: Option[String], id: Option[T], parent: Option[RouteFilterContext[_]]) {
  @tailrec
  private def _contextPath(context: RouteFilterContext[_], sequence: Seq[String]): Seq[String] = {
    if(!context.parent.isDefined) {
      sequence
    } else {
      val parent = context.parent.get
      _contextPath(parent, sequence :+ parent.path)
    }
  }

  lazy val contextPath: String = _contextPath(this, Seq(this.path)).reverse.mkString("/")
}

trait RestRouteFilter[T] {
  def filterTraverse( requestHeader: RequestHeader, context: RouteFilterContext[T], next: () => Option[Handler] ) : Option[Handler]
  def filterRead( requestHeader: RequestHeader, context: RouteFilterContext[T], next: () => Option[EssentialAction] ) : Option[EssentialAction]
  def filterWrite( requestHeader: RequestHeader, context: RouteFilterContext[T], next: () => Option[EssentialAction] ) : Option[EssentialAction]
  def filterUpdate( requestHeader: RequestHeader, context: RouteFilterContext[T], next: () => Option[EssentialAction] ) : Option[EssentialAction]
  def filterDelete( requestHeader: RequestHeader, context: RouteFilterContext[T], next: () => Option[EssentialAction] ) : Option[EssentialAction]
  def filterCustom( requestHeader: RequestHeader, context: RouteFilterContext[T], next: () => Option[EssentialAction] ) : Option[EssentialAction]
  def filterList( requestHeader: RequestHeader, context: RouteFilterContext[T], next: () => Option[EssentialAction] ) : Option[EssentialAction]
  def filterCreate( requestHeader: RequestHeader, context: RouteFilterContext[T], next: () => Option[EssentialAction] ) : Option[EssentialAction]
}

trait SimpleRestRouteFilter[T] extends RestRouteFilter[T] {
  def filter( actionType: RestRouteActionType )
            ( requestHeader: RequestHeader,
              context: RouteFilterContext[T],
              next: () => Option[EssentialAction] ) : Option[EssentialAction]

  def filterRead( requestHeader: RequestHeader, context: RouteFilterContext[T], next: () => Option[EssentialAction] ) =
    filter(Read)(requestHeader, context, next)
  def filterWrite( requestHeader: RequestHeader, context: RouteFilterContext[T], next: () => Option[EssentialAction] ) =
    filter(Write)(requestHeader, context, next)
  def filterUpdate( requestHeader: RequestHeader, context: RouteFilterContext[T], next: () => Option[EssentialAction] ) =
    filter(Update)(requestHeader, context, next)
  def filterDelete( requestHeader: RequestHeader, context: RouteFilterContext[T], next: () => Option[EssentialAction] ) =
    filter(Delete)(requestHeader, context, next)
  def filterCustom( requestHeader: RequestHeader, context: RouteFilterContext[T], next: () => Option[EssentialAction] ) =
    filter(Custom)(requestHeader, context, next)
  def filterList( requestHeader: RequestHeader, context: RouteFilterContext[T], next: () => Option[EssentialAction] ) =
    filter(List)(requestHeader, context, next)
  def filterCreate( requestHeader: RequestHeader, context: RouteFilterContext[T], next: () => Option[EssentialAction] ) =
    filter(Create)(requestHeader, context, next)
}


case class NoopFilter() extends SimpleRestRouteFilter[Any] {
  def filter( actionType: RestRouteActionType )( requestHeader: RequestHeader,
                                                 context: RouteFilterContext[Any],
                                                 next: ()=> Option[EssentialAction] ) =
    next()

  def filterTraverse( requestHeader: RequestHeader, context: RouteFilterContext[Any], next: () => Option[Handler] ) =
    next()
}


trait DefaultRestRouteFilter[T] extends RestRouteFilter[T] {
  def filterTraverse( requestHeader: RequestHeader, context: RouteFilterContext[T], next: () => Option[Handler] ) = next()
  def filterRead( requestHeader: RequestHeader, context: RouteFilterContext[T], next: () => Option[EssentialAction] ) = next()
  def filterWrite( requestHeader: RequestHeader, context: RouteFilterContext[T], next: () => Option[EssentialAction] ) = next()
  def filterUpdate( requestHeader: RequestHeader, context: RouteFilterContext[T], next: () => Option[EssentialAction] ) = next()
  def filterDelete( requestHeader: RequestHeader, context: RouteFilterContext[T], next: () => Option[EssentialAction] ) = next()
  def filterCustom( requestHeader: RequestHeader, context: RouteFilterContext[T], next: () => Option[EssentialAction] ) = next()
  def filterList( requestHeader: RequestHeader, context: RouteFilterContext[T], next: () => Option[EssentialAction] ) = next()
  def filterCreate( requestHeader: RequestHeader, context: RouteFilterContext[T], next: () => Option[EssentialAction] ) = next()
}
