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

package uk.gov.hmrc.vatsignupfrontend.controllers.principal

import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.Status
import play.api.mvc.AnyContentAsFormUrlEncoded
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.http.InternalServerException
import uk.gov.hmrc.play.test.UnitSpec
import uk.gov.hmrc.vatsignupfrontend.SessionKeys
import uk.gov.hmrc.vatsignupfrontend.config.featureswitch.CompanyNameJourney
import uk.gov.hmrc.vatsignupfrontend.config.mocks.MockControllerComponents
import uk.gov.hmrc.vatsignupfrontend.forms.CompanyNumberForm._
import uk.gov.hmrc.vatsignupfrontend.helpers.TestConstants._
import uk.gov.hmrc.vatsignupfrontend.services.mocks.MockGetCompanyNameService

class CaptureCompanyNumberControllerSpec extends UnitSpec with GuiceOneAppPerSuite with MockControllerComponents
  with MockGetCompanyNameService {

  override def beforeEach(): Unit = {
    super.beforeEach()
    disable(CompanyNameJourney)
  }

  object TestCaptureCompanyNumberController extends CaptureCompanyNumberController(
    mockControllerComponents,
    mockGetCompanyNameService
  )

  lazy val testGetRequest = FakeRequest("GET", "/company-registration-number")

  def testPostRequest(companyNumberVal: String): FakeRequest[AnyContentAsFormUrlEncoded] =
    FakeRequest("POST", "/company-registration-number").withFormUrlEncodedBody(companyNumber -> companyNumberVal)


  "Calling the show action of the Capture Company Number controller" should {
    "go to the Capture Company number page" in {
      mockAuthAdminRole()

      val result = TestCaptureCompanyNumberController.show(testGetRequest)
      status(result) shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      charset(result) shouldBe Some("utf-8")
    }
  }


  "Calling the submit action of the Capture Company Number controller" when {
    "company name journey is not enabled" should {
      "form successfully submitted" should {
        "goto confirm Company number page" in {
          mockAuthAdminRole()

          val request = testPostRequest(testCompanyNumber)

          val result = TestCaptureCompanyNumberController.submit(request)
          status(result) shouldBe Status.SEE_OTHER
          redirectLocation(result) shouldBe Some(routes.ConfirmCompanyNumberController.show().url)

          result.session(request).get(SessionKeys.companyNumberKey) shouldBe Some(testCompanyNumber)
        }
      }

      "form unsuccessfully submitted" should {
        "reload the page with errors" in {
          mockAuthAdminRole()

          val result = TestCaptureCompanyNumberController.submit(testPostRequest("invalid"))
          status(result) shouldBe Status.BAD_REQUEST
          contentType(result) shouldBe Some("text/html")
          charset(result) shouldBe Some("utf-8")
        }
      }
    }
    "company name journey is enabled" when {
      "get company name returned successfully" should {
        "goto confirm company and store crn and name in session" in {
          mockAuthAdminRole()
          enable(CompanyNameJourney)

          mockGetCompanyNameSuccess(testCompanyNumber)

          val request = testPostRequest(testCompanyNumber)

          val result = TestCaptureCompanyNumberController.submit(request)
          status(result) shouldBe Status.SEE_OTHER
          redirectLocation(result) shouldBe Some(routes.ConfirmCompanyController.show().url)

          result.session(request).get(SessionKeys.companyNumberKey) shouldBe Some(testCompanyNumber)
          result.session(request).get(SessionKeys.companyNameKey) shouldBe Some(testCompanyName)
        }
      }
      "get company name returned not found" should {
        "goto company name not found page" in {
          mockAuthAdminRole()
          enable(CompanyNameJourney)

          mockGetCompanyNameNotFound(testCompanyNumber)

          val request = testPostRequest(testCompanyNumber)

          val result = TestCaptureCompanyNumberController.submit(request)
          status(result) shouldBe Status.SEE_OTHER
          redirectLocation(result) shouldBe Some(routes.CompanyNameNotFoundController.show().url)
        }
      }
      "get company name fails" should {
        "throw an InternalServerException" in {
          mockAuthAdminRole()
          enable(CompanyNameJourney)

          mockGetCompanyNameFailure(testCompanyNumber)

          val request = testPostRequest(testCompanyNumber)

          intercept[InternalServerException](await(TestCaptureCompanyNumberController.submit(request)))
        }
      }
    }
  }

}
