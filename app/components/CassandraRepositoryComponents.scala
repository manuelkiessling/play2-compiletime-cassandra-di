package components

import cassandra.{CassandraConnector, CassandraConnectionUri}
import com.datastax.driver.core.Session
import models.ProductModel
import play.api.inject.ApplicationLifecycle
import play.api.{Configuration, Environment, Mode}
import repositories.{Repository, ProductsRepository}
import scala.concurrent.Future

trait CassandraRepositoryComponents {
  // These will be filled by Play's built-in components; should be `def` to avoid initialization problems
  def environment: Environment
  def configuration: Configuration
  def applicationLifecycle: ApplicationLifecycle

  lazy private val cassandraSession: Session = {
    val uriString = environment.mode match {
      case Mode.Prod  => "cassandra://localhost:9042/prod"
      case _          => "cassandra://localhost:9042/test"
    }
    val session: Session = CassandraConnector.createSessionAndInitKeyspace(
      CassandraConnectionUri(uriString)
    )
    // Shutdown the client when the app is stopped or reloaded
    applicationLifecycle.addStopHook(() => Future.successful(session.close()))
    session
  }

  lazy val productsRepository: Repository[ProductModel, Int] = {
    new ProductsRepository(cassandraSession)
  }
}
