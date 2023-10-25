package com.example.inventory

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.inventory.data.Item
import com.example.inventory.data.ItemDao
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class InventoryViewModel(private val itemDao:ItemDao): ViewModel(){

    val allItems: LiveData<List<Item>> = itemDao.getAllItem().asLiveData()

    fun retrieveItem(id:Int): LiveData<Item>{
        return itemDao.getItem(id).asLiveData()
    }


    //// Insert

    private fun insertItem(item: Item){
        /** To interact with the database off the main thread, start a coroutine and call the DAO method within it.
         * Inside the insertItem() method, use the viewModelScope.launch to start a coroutine in the ViewModelScope.
         * Inside the launch function, call the suspend function insert() on itemDao passing in the item.
         * The ViewModelScope is an extension property to the ViewModel class that automatically cancels its child coroutines when the ViewModel is destroyed. */
        viewModelScope.launch {
            itemDao.insert(item)
        }
    }

    private fun getNewItemEntry(itemName:String,itemPrice:String,itemQuantity:String):Item{
        return Item(
            itemName = itemName,
            itemPrice = itemPrice.toDouble(),
            quantityInStock = itemQuantity.toInt()
        )
    }

    fun addNewItem(itemName: String, itemPrice: String, itemCount: String){
        val newItem = getNewItemEntry(itemName,itemPrice,itemCount)
        insertItem(newItem)
    }

    fun isEntryValid(itemName: String, itemPrice: String, itemCount: String): Boolean {
        if (itemName.isBlank() || itemPrice.isBlank() || itemCount.isBlank()) {
            return false
        }
        return true
    }

    //// \\ Insert


    // this updateItem() is used by both to edit and sell item.
    private fun updateItem(item:Item){
        viewModelScope.launch {
            itemDao.update(item)
        }
    }

    fun sellItem(item: Item) {
        if(item.quantityInStock > 0){
            /** The copy() function is provided by default to all the instances of data classes. This function is used to copy an object for changing some of its properties, but keeping the rest of the properties unchanged.*/
            val newItem = item.copy(quantityInStock = item.quantityInStock - 1)
            updateItem(newItem)
        }
    }

    // this is for disabling the sell button in UI
    fun isStockAvailable(item: Item): Boolean {
        return (item.quantityInStock > 0)
    }

    fun deleteItem(item:Item){
        viewModelScope.launch{
            itemDao.delete(item)
        }
    }

    private fun getUpdatedItemEntry(itemId:Int, itemName: String, itemPrice: String, itemCount: String): Item{
        return Item(itemId,itemName,itemPrice.toDouble(),itemCount.toInt())
    }


    fun updateItemFunc(
        itemId: Int,
        itemName: String,
        itemPrice: String,
        itemCount: String
    ) {
        val updatedItem = getUpdatedItemEntry(itemId,itemName,itemPrice,itemCount)
        updateItem(updatedItem)
    }

}


class InventoryViewModelFactory(private val itemDao:ItemDao): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(InventoryViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return InventoryViewModel(itemDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}