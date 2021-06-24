package ru.kireev.mir.registrarlizaalert.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import org.koin.android.ext.android.inject
import ru.kireev.mir.registrarlizaalert.R
import ru.kireev.mir.registrarlizaalert.databinding.FragmentAddManuallyBinding
import ru.kireev.mir.registrarlizaalert.presentation.viewmodel.AddManuallyViewModel
import ru.kireev.mir.registrarlizaalert.ui.util.args
import ru.kireev.mir.registrarlizaalert.ui.util.getTrimmedString
import ru.kireev.mir.registrarlizaalert.ui.util.setActionBarTitle

class AddManuallyFragment : Fragment(R.layout.fragment_add_manually) {

    private val binding: FragmentAddManuallyBinding by viewBinding()

    private val viewModel: AddManuallyViewModel by inject()

    private var index: Int by args()
    private var volunteerId: Int? by args()

    companion object {
        @JvmStatic
        fun newInstance(index: Int, volunteerId: Int?) =
            AddManuallyFragment().apply {
                this.index = index
                this.volunteerId = volunteerId
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        viewModel.loadVolunteersData(volunteerId)
        viewModel.error.observe(viewLifecycleOwner, { error ->
            showShortSnack(view, getString(error))
        })
        binding.buttonSaveData.setOnClickListener { onClickSaveData() }
    }

    private fun onClickSaveData() {
        binding.run {
            val fullName = editTextFullName.getTrimmedString()
            val callSign = editTextCallSign.getTrimmedString()
            val nickName = editTextForumNickname.getTrimmedString()
            val region = editTextRegion.getTrimmedString()
            val phoneNumber = editTextPhoneNumber.getTrimmedString()
            val car = editTextCar.getTrimmedString()

            viewModel.saveData(
                fullName = fullName,
                callSign = callSign,
                nickName = nickName,
                region = region,
                phoneNumber = phoneNumber,
                car = car,
                index = index
            )
        }
    }

    override fun onStart() {
        super.onStart()
        setActionBarTitle(R.string.actionbar_label_add_manually)
    }
}