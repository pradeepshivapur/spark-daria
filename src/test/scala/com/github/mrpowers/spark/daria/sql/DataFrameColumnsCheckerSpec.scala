package com.github.mrpowers.spark.daria.sql

import org.scalatest.FunSpec
import SparkSessionExt._
import org.apache.spark.sql.types.StringType

class DataFrameColumnsCheckerSpec
    extends FunSpec
    with SparkSessionTestWrapper {

  describe("missingColumns") {

    it("returns the columns missing from a DataFrame") {

      val sourceDF = spark.createDF(
        List(
          ("jets", "football"),
          ("nacional", "soccer")
        ), List(
          ("team", StringType, true),
          ("sport", StringType, true)
        )
      )

      val requiredColNames = Seq("team", "sport", "country", "city")

      val c = new DataFrameColumnsChecker(sourceDF, requiredColNames)

      assert(c.missingColumns === List("country", "city"))

    }

    it("returns the empty list if columns aren't missing") {

      val sourceDF = spark.createDF(
        List(
          ("jets", "football"),
          ("nacional", "soccer")
        ), List(
          ("team", StringType, true),
          ("sport", StringType, true)
        )
      )

      val requiredColNames = Seq("team")

      val c = new DataFrameColumnsChecker(sourceDF, requiredColNames)

      assert(c.missingColumns === List())

    }

  }

  describe("#missingColumnsMessage") {

    it("provides a descriptive message when columns are missing") {

      val sourceDF = spark.createDF(
        List(
          ("jets", "football"),
          ("nacional", "soccer")
        ), List(
          ("team", StringType, true),
          ("sport", StringType, true)
        )
      )

      val requiredColNames = Seq("team", "sport", "country", "city")

      val v = new DataFrameColumnsChecker(sourceDF, requiredColNames)

      val expected = "The [country, city] columns are not included in the DataFrame with the following columns [team, sport]"

      assert(v.missingColumnsMessage() === expected)

    }

  }

  describe("#validatePresenceOfColumns") {

    it("throws an exception if columns are missing from a DataFrame") {

      val sourceDF = spark.createDF(
        List(
          ("jets", "football"),
          ("nacional", "soccer")
        ), List(
          ("team", StringType, true),
          ("sport", StringType, true)
        )
      )

      val requiredColNames = Seq("team", "sport", "country", "city")

      val v = new DataFrameColumnsChecker(sourceDF, requiredColNames)

      intercept[MissingDataFrameColumnsException] {
        v.validatePresenceOfColumns()
      }

    }

    it("does nothing if columns aren't missing") {

      val sourceDF = spark.createDF(
        List(
          ("jets", "football"),
          ("nacional", "soccer")
        ), List(
          ("team", StringType, true),
          ("sport", StringType, true)
        )
      )

      val requiredColNames = Seq("team")

      val v = new DataFrameColumnsChecker(sourceDF, requiredColNames)
      v.validatePresenceOfColumns()

    }

  }

}
