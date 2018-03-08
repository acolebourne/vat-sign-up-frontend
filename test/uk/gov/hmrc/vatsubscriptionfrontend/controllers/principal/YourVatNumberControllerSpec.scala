/*
 * Copyright 2018 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.vatsubscriptionfrontend.controllers.principal

import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.Status
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.http.InternalServerException
import uk.gov.hmrc.play.test.UnitSpec
import uk.gov.hmrc.vatsubscriptionfrontend.config.mocks.MockControllerComponents
import uk.gov.hmrc.vatsubscriptionfrontend.helpers.TestConstants._
import uk.gov.hmrc.vatsubscriptionfrontend.services.mocks.MockStoreVatNumberService

class YourVatNumberControllerSpec extends UnitSpec with GuiceOneAppPerSuite with MockControllerComponents
  with MockStoreVatNumberService {

  object TestYourVatNumberController extends YourVatNumberController(mockControllerComponents, mockStoreVatNumberService)

  lazy val testGetRequest = FakeRequest("GET", "/confirm-vat-number")

  lazy val testPostRequest: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest("POST", "/confirm-vat-number")

  "Calling the show action of the Confirm Vat Number controller" should {
    "go to the Confirm Vat number page" in {
      mockAuthRetrieveVatDecEnrolment()

      val result = TestYourVatNumberController.show(testGetRequest)
      status(result) shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      charset(result) shouldBe Some("utf-8")
    }
  }

  "Calling the submit action of the Confirm Vat Number controller" when {
    "store vat is successful" should {
      "return unimplemented" in {
        mockAuthRetrieveVatDecEnrolment()
        mockStoreVatNumberSuccess(vatNumber = testVatNumber)

        val result = TestYourVatNumberController.submit(testPostRequest)
        status(result) shouldBe Status.NOT_IMPLEMENTED
      }
    }
    "store vat is unsuccessful" should {
      "throw internal server exception" in {
        mockAuthRetrieveVatDecEnrolment()
        mockStoreVatNumberFailure(vatNumber = testVatNumber)

        intercept[InternalServerException] {
          await(TestYourVatNumberController.submit(testPostRequest))
        }
      }
    }
  }

}