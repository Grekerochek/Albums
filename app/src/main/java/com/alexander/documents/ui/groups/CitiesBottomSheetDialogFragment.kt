package com.alexander.documents.ui.groups

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.alexander.documents.R
import com.alexander.documents.entity.City
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.bottom_sheet_layout.*

/**
 * author alex
 */
class CitiesBottomSheetDialogFragment : BottomSheetDialogFragment() {

    private val cities: List<City> by lazy(LazyThreadSafetyMode.NONE) {
        requireArguments()[ARG_CITIES] as List<City>
    }

    private val citiesAdapter: CitiesAdapter by lazy(LazyThreadSafetyMode.NONE) {
        CitiesAdapter(::onCityClick)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun getTheme(): Int {
        return R.style.BottomSheetDialog
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState)
        bottomSheetDialog.setOnShowListener { dialog ->
            val bottomSheet = (dialog as BottomSheetDialog).findViewById<FrameLayout>(R.id.design_bottom_sheet)
            BottomSheetBehavior.from(bottomSheet!!).state = BottomSheetBehavior.STATE_EXPANDED
            BottomSheetBehavior.from(bottomSheet).skipCollapsed = true
            BottomSheetBehavior.from(bottomSheet).isHideable = true
        }
        return bottomSheetDialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        citiesRecyclerView.adapter = citiesAdapter
        citiesAdapter.cities = cities.toMutableList()
        dismissView.setOnClickListener { dismiss() }
    }

    override fun onDetach() {
        super.onDetach()
    }

    private fun onCityClick(position: Int, city: City) {
        citiesAdapter.selectCity(position)
        dismiss()
    }

    companion object {
        private const val ARG_CITIES = "arg_group"

        fun newInstance(cities: List<City>) = CitiesBottomSheetDialogFragment()
            .apply {
                val args = Bundle()
                args.putSerializable(ARG_CITIES, ArrayList<City>(cities))
                arguments = args
            }
    }
}