package io.sorne.tlang.lsp

import java.io.{BufferedReader, InputStreamReader, PrintStream}
import java.net.ServerSocket

import org.json4s.native.Serialization.write
import org.json4s.native.{JsonMethods, Serialization}
import org.json4s.{Formats, NoTypeHints}

object LSPServer {

  implicit val formats: AnyRef with Formats = Serialization.formats(NoTypeHints)


  def startLSPServer(port: Int): Unit = {
    try {
      val server = new ServerSocket(port)
      println("Serve initialized:")
      val client = server.accept

      val in = new BufferedReader(new InputStreamReader(client.getInputStream))
      val out = new PrintStream(client.getOutputStream)

      var stop = false
      var header = true
      var length: Int = 0
      while (!stop) {
        if (header) {
          val line = in.readLine()
          println("[" + line + "]")
          if (line.startsWith("Content-Length:")) {
            length = line.substring(15).trim.toInt
          }
          if (line.equals("\n") || line.equals("\r\n") || line.isEmpty) {
            header = false
          }
        } else {
          val data = new Array[Char](length)
          in.read(data, 0, length)
          val line = String.valueOf(data)
          println(line)
          println("*************************************************")
          val values = JsonMethods.parse(line).values.asInstanceOf[Map[String, Any]]
          //          val values = read[Message](line)
          val id: Int = if (values.contains("id")) values("id").toString.toInt else 0
          val message = Request(values("jsonrpc").toString, id, values("method").toString, values("params").asInstanceOf[Map[String, Any]])
          ParseMessage.parseMessage(message) match {
            case Left(_) =>
            case Right(value) =>
              value match {
                case Some(value) =>
                  val res = write(value)
                  val str = new StringBuilder("Content-Length: " + res.length + "\r\n")
                  str ++= "\r\n"
                  str ++= res
                  print(str.toString())
                  out.print(str.toString())
                  out.flush()
                  println("\n-----------------------------------------------------")
                case None =>
              }
          }
          header = true
        }

      }
      client.close()
      server.close()
      println("Server closing:")
    }

    catch {
      case e: Exception =>
        e.printStackTrace()
        System.exit(1)
    }

  }
}
