import java.io.File
import cassandra.{CassandraConnector, CassandraConnectionUri}
import org.scalatest.BeforeAndAfter
import org.scalatestplus.play._
import play.api
import play.api.{Mode, Environment, ApplicationLoader}

class IntegrationSpec extends PlaySpec with OneBrowserPerSuite with OneServerPerSuite with HtmlUnitFactory with BeforeAndAfter {

  before {
    val uri = CassandraConnectionUri("cassandra://localhost:9042/test")
    val session = CassandraConnector.createSessionAndInitKeyspace(uri)
    val query = "INSERT INTO products (id, name) VALUES (1,  'Chair');"
    session.execute(query)
    session.close()
  }

  override implicit lazy val app: api.Application =
    new AppLoader().load(
      ApplicationLoader.createContext(
        new Environment(
          new File("."), ApplicationLoader.getClass.getClassLoader, Mode.Test)
      )
    )

  "Application" should {

    "work from within a browser and tell us about the first product" in {

      go to "http://localhost:" + port

      pageSource must include ("Your new application is ready. The name of product #1 is Chair.")
    }
  }
}
