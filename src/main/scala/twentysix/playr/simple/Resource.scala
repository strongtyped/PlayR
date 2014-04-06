package twentysix.playr.simple

import play.api.mvc.EssentialAction
import twentysix.playr.core
import twentysix.playr.core.ResourceAction
import reflect.runtime.universe.{Type,TypeTag,typeOf}
import scala.language.implicitConversions

trait Resource[R] extends core.ResourceTrait[R] {
  def parseId(sid: String) = fromId(sid)
  
  def handleAction(id: R, f: Function1[R, EssentialAction]) = f(id)
  def fromId(sid: String): Option[R]
}

object Resource {
  implicit def simpleResourceAction[R, C<:Resource[R]](f: R=> EssentialAction)(implicit tt: TypeTag[(R=> EssentialAction)]) = 
    new ResourceAction[C]{
      def handleAction(controller: C, id: R): EssentialAction = controller.handleAction(id, f)
      def getType: Type = tt.tpe 
    }
}

trait ResourceRead extends core.ResourceRead {
  this: core.BaseResource => 
  def readResource(id: IdentifierType) = read(id)
  def listResource = list

  def read(id: IdentifierType): EssentialAction
  def list: EssentialAction
}

trait ResourceWrite extends core.ResourceWrite{
  this: core.BaseResource => 
  def writeResource(id: IdentifierType) = write(id) 

  def write(id: IdentifierType): EssentialAction
}

trait ResourceDelete extends core.ResourceDelete {
  this: core.BaseResource => 
  def deleteResource(id: IdentifierType) = delete(id) 

  def delete(id: IdentifierType): EssentialAction
}

trait ResourceUpdate extends core.ResourceUpdate {
  this: core.BaseResource => 
  def updateResource(id: IdentifierType) = update(id) 

  def update(id: IdentifierType): EssentialAction
}

trait ResourceCreate extends core.ResourceCreate {
  this: core.BaseResource => 
  def createResource = create 

  def create: EssentialAction
}

//-------------------------
//---- Shortcut traits ----
//-------------------------

trait RestReadController[R] extends Resource[R]
                               with ResourceRead

/**
 * Read and write controller: implements GET, POST and PATCH for partial updates
 */
trait RestRwController[R] extends Resource[R]
                             with ResourceCreate
                             with ResourceRead
                             with ResourceUpdate

/**
 * Same as RestRWController plus DELETE method
 */
trait RestRwdController[R] extends RestRwController[R]
                              with ResourceDelete

/**
 * Classic rest controller: handle GET, POST, PUT and DELETE http methods
 */
trait RestCrudController[R] extends Resource[R]
                               with ResourceCreate
                               with ResourceRead
                               with ResourceDelete
                               with ResourceWrite