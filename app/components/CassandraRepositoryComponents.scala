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

  /*

  It's important to make these vals lazy - if not, they get initialized in any case when
  running the code - but that's not welcome in certain scenarios.

  E.g., in ApplicationSpec, we create a FakeApplicationComponents which extends AppComponents,
  and therefore this trait. Within FakeApplicationComponents, we override productsRepository,
  replacing the actual thing with a mock, thus not needing an actual database.

  However, if these vals would not be lazy, they would be evaluated even though productsRepository
  will be overridden with a mock eventually - and thus, a database connection would be tried although
  it's not needed.

   */
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
