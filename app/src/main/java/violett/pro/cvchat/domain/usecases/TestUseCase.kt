package violett.pro.cvchat.domain.usecases

import violett.pro.cvchat.domain.model.errors.TestError
import violett.pro.cvchat.domain.util.CustomResult


// supposed to be a repo
fun String.test(isSuccess: Boolean) : CustomResult<String, TestError> {
    return if (isSuccess){
        CustomResult.Success(this.uppercase())
    } else {
        CustomResult.Failure(TestError.Validation.PASSWORD_TOO_SHORT)
    }

}


class TestUseCase(private val repo : String = "") {
    suspend operator fun invoke(isSuccess: Boolean? = null): CustomResult<String, TestError> {
        if (isSuccess == null) {
            return CustomResult.Failure(TestError.Validation.INVALID_EMAIL)
        }
        return repo.test(isSuccess)

    }
}