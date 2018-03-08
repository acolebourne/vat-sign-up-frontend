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
import uk.gov.hmrc.auth.core.retrieve.EmptyRetrieval
import uk.gov.hmrc.http.InternalServerException
import uk.gov.hmrc.play.test.UnitSpec
import uk.gov.hmrc.vatsubscriptionfrontend.SessionKeys
import uk.gov.hmrc.vatsubscriptionfrontend.config.mocks.MockControllerComponents
import uk.gov.hmrc.vatsubscriptionfrontend.helpers.TestConstants._
import uk.gov.hmrc.vatsubscriptionfrontend.services.mocks.MockStoreEmailAddressService

import scala.concurrent.Future

class ConfirmEmailControllerSpec extends UnitSpec with GuiceOneAppPerSuite with MockControllerComponents
  with MockStoreEmailAddressService {

  object TestConfirmEmailController extends ConfirmEmailController(mockControllerComponents, mockStoreEmailAddressService)

  lazy val testGetRequest = FakeRequest("GET", "/confirm-email")

  lazy val testPostRequest: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest("POST", "/confirm-email")

  "Calling the show action of the Confirm Email controller" when {
    "there is a email in the session" should {
      "show the Confirm Email page" in {
        mockAuthorise(retrievals = EmptyRetrieval)(Future.successful(Some("")))

        val request = testGetRequest.withSession(SessionKeys.vatNumberKey -> testVatNumber, SessionKeys.emailKey -> testEmail)

        val result = TestConfirmEmailController.show(request)

        status(result) shouldBe Status.OK
        contentType(result) shouldBe Some("text/html")
        charset(result) shouldBe Some("utf-8")
      }
    }

    "there isn't a email in the session" should {
      "redirect to Capture Email page" in {
        mockAuthorise(retrievals = EmptyRetrieval)(Future.successful(Some("")))

        val result = TestConfirmEmailController.show(testGetRequest.withSession(SessionKeys.vatNumberKey -> testVatNumber))
        status(result) shouldBe Status.SEE_OTHER
        redirectLocation(result) shouldBe Some(routes.CaptureEmailController.show().url)
      }
    }
  }

  "Calling the submit action of the Confirm Email controller" when {
    "email and vat number is in session and store call is successful" when {
      "email is not verified" should {
        "redirect to Verify Email page" in {
          mockAuthorise(retrievals = EmptyRetrieval)(Future.successful(Some("")))
          mockStoreEmailAddressSuccess(vatNumber = testVatNumber, email = testEmail)(emailVerified = false)

          val result = TestConfirmEmailController.submit(testPostRequest.withSession(SessionKeys.emailKey -> testEmail,
            SessionKeys.vatNumberKey -> testVatNumber))

          status(result) shouldBe Status.SEE_OTHER
          redirectLocation(result) shouldBe Some(routes.VerifyEmailController.show().url)

        }
      }
      "email is verified" should {
        "redirect to Terms page" in {
          mockAuthorise(retrievals = EmptyRetrieval)(Future.successful(Some("")))
          mockStoreEmailAddressSuccess(vatNumber = testVatNumber, email = testEmail)(emailVerified = true)

          val result = TestConfirmEmailController.submit(testPostRequest.withSession(SessionKeys.emailKey -> testEmail,
            SessionKeys.vatNumberKey -> testVatNumber))

          status(result) shouldBe Status.NOT_IMPLEMENTED

        }
      }
    }
    "email and vat number is in session but store call is unsuccessful" should {
      "throw Internal Server Error" in {
        mockAuthorise(retrievals = EmptyRetrieval)(Future.successful(Some("")))
        mockStoreEmailAddressFailure(vatNumber = testVatNumber, email = testEmail)

        intercept[InternalServerException] {
          await(TestConfirmEmailController.submit(testPostRequest.withSession(
            SessionKeys.vatNumberKey -> testVatNumber,
            SessionKeys.emailKey -> testEmail
          )))
        }
      }
    }
    "vat number is not in session" should {
      "redirect to Capture Vat number page" in {
        mockAuthorise(retrievals = EmptyRetrieval)(Future.successful(Some("")))

        val result = TestConfirmEmailController.submit(testPostRequest.withSession(SessionKeys.emailKey -> testEmail))
        status(result) shouldBe Status.SEE_OTHER
        redirectLocation(result) shouldBe Some(routes.YourVatNumberController.show().url)

      }
    }
    "email is not in session" should {
      "redirect to Capture Email page" in {
        mockAuthorise(retrievals = EmptyRetrieval)(Future.successful(Some("")))
        mockAuthRetrieveAgentEnrolment()

        val result = TestConfirmEmailController.submit(testPostRequest.withSession(SessionKeys.vatNumberKey -> testVatNumber))
        status(result) shouldBe Status.SEE_OTHER
        redirectLocation(result) shouldBe Some(routes.CaptureEmailController.show().url)
      }
    }
  }

}
