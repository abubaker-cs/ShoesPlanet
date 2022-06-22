package org.abubaker.shoesplanet.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import org.abubaker.shoesplanet.BaseApplication
import org.abubaker.shoesplanet.R
import org.abubaker.shoesplanet.databinding.FragmentAddShoeBinding
import org.abubaker.shoesplanet.model.Shoe
import org.abubaker.shoesplanet.ui.viewmodel.ShoeViewModel
import org.abubaker.shoesplanet.ui.viewmodel.ShoeViewModelFactory


/**
 * A simple [Fragment] subclass.
 * Use the [AddShoeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddShoeFragment : Fragment() {

    private val navigationArgs: AddShoeFragmentArgs by navArgs()

    private var _binding: FragmentAddShoeBinding? = null

    private lateinit var shoe: Shoe

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    // TO DO: Refactor the creation of the view model to take an instance of
    //  ShoeViewModelFactory. The factory should take an instance of the Database retrieved
    //  from BaseApplication
    private val viewModel: ShoeViewModel by activityViewModels {

        ShoeViewModelFactory(
            (activity?.application as BaseApplication).database.shoeDao()
        )

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        // return inflater.inflate(R.layout.fragment_add_shoe, container, false)
        // Inflate the layout for this fragment
        _binding = FragmentAddShoeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val id = navigationArgs.id
        if (id > 0) {

            // TO DO: Observe a Shoe that is retrieved by id, set the shoes variable,
            //  and call the bindShoe method

            viewModel.getShoe(id).observe(this.viewLifecycleOwner) { selectedShoe ->

                shoe = selectedShoe

                bindShoe(shoe)

            }

            binding.deleteBtn.visibility = View.VISIBLE
            binding.deleteBtn.setOnClickListener {
                deleteShoe(shoe)
            }
        } else {
            binding.saveBtn.setOnClickListener {
                addShoe()
            }
        }
    }

    private fun deleteShoe(shoe: Shoe) {
        viewModel.deleteShoe(shoe)

        findNavController().navigate(
            R.id.action_addShoeFragment_to_shoeListFragment
        )

    }

    private fun addShoe() {

        if (isValidEntry()) {

            viewModel.addShoe(
                binding.brandNameInput.text.toString(),
                binding.modelInput.text.toString(),
                binding.priceInput.text.toString(),
                binding.inStockCheckbox.isChecked,
                binding.notesInput.text.toString()
            )

            findNavController().navigate(
                R.id.action_addShoeFragment_to_shoeListFragment
            )

        }
    }

    private fun updateShoe() {
        if (isValidEntry()) {

            viewModel.updateShoe(
                id = navigationArgs.id,
                model = binding.shoeModelInput.text.toString(),
                brand = binding.brandNameInput.text.toString(),
                price = binding.shoePriceInput.text.toString(),
                color = binding.shoeColorInput.text.toString(),
                size = binding.shoeSizeInput.text.toString(),
                inStock = binding.inStockCheckbox.isChecked,
                notes = binding.notesInput.text.toString()
            )

            findNavController().navigate(
                R.id.action_addShoeFragment_to_shoeListFragment
            )
        }
    }

    private fun bindShoe(shoe: Shoe) {
        binding.apply {
            shoeModelInput.setText(shoe.modelNumber, TextView.BufferType.SPANNABLE)
            brandNameInput.setText(shoe.brandName, TextView.BufferType.SPANNABLE)
            shoePriceInput.setText(shoe.shoePrice, TextView.BufferType.SPANNABLE)
            shoeColorInput.setText(shoe.shoeColor, TextView.BufferType.SPANNABLE)
            shoeSizeInput.setText(shoe.shoeSize, TextView.BufferType.SPANNABLE)
            inStockCheckbox.isChecked = shoe.inStock
            notesInput.setText(shoe.notes, TextView.BufferType.SPANNABLE)

            saveBtn.setOnClickListener {
                updateShoe()
            }


        }

    }

    private fun isValidEntry() = viewModel.isValidEntry(
        binding.brandNameInput.text.toString(),
        binding.shoeModelInput.text.toString()
    )

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}