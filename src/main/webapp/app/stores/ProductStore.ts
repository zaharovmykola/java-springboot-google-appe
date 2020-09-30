import {action, computed, observable} from 'mobx'
import Product from '../models/ProductModel'
import commonStore from './CommonStore'
import Category from "app/models/CategoryModel";

class ProductStore {

    private HTTP_STATUS_OK: number = 200
    private HTTP_STATUS_CREATED: number = 201
    private allowGetPriceBounds: boolean = true
    private allowGetQuantityBounds: boolean = true

    @observable currentProduct: Product = new Product()
    @observable currentProductId: BigInteger = null
    @observable products: Array<Product> = []
    @observable currentProductImage: string = ''
    // наблюдаемые свойства для фильтра товаров
    @observable orderBy: string = 'id'
    @observable sortingDirection: string = 'DESC'
    @observable priceFrom: number = null
    @observable priceTo: number = null

    @observable quantityFrom: number = null
    @observable quantityTo: number = null

    @observable categories: Array<number> = []

    @observable priceFromBound: number = 0
    @observable priceToBound: number = 1000000

    @observable quantityFromBound: number = 0
    @observable quantityToBound: number = 1000000

    @computed get isFilterDataPresent() {
        return (this.orderBy && this.sortingDirection)
            || (this.priceFrom && this.priceTo)
            || (this.quantityFrom && this.quantityTo)
            || this.categories.length > 0
    }

    @action setCurrentProduct(product: Product) {
        this.currentProduct = product
    }

    @action setCurrentProductId(id: BigInteger) {
        this.currentProductId = id
    }

    @action setProductTitle(title: string) {
        this.currentProduct.title = title
    }

    @action setProductCategory(categoryId: BigInteger) {
        this.currentProduct.categoryId = categoryId
    }

    @action setProductDescription(description: string) {
        this.currentProduct.description = description
    }

    @action setProductPrice(price: number) {
        this.currentProduct.price = price
    }

    @action setProductQuantity(quantity: number) {
        this.currentProduct.quantity = quantity
    }

    @action setProductImage(image: string) {
        this.currentProductImage = image
        this.currentProduct.image = image
    }

    @action setOrderBy(fieldName: string) {
        this.orderBy = fieldName
    }

    @action setSortingDirection(direction: string) {
        this.sortingDirection = direction
    }

    private handlePriceBoundsValues () {
        if (this.priceFrom && this.priceTo) {
            this.allowGetPriceBounds = false
            setTimeout(() => {
                if(this.allowGetPriceBounds) {
                    this.fetchProductPriceBounds()
                }
            }, 3500)
            this.getFilteredProducts()
        } else {
            this.allowGetPriceBounds = true
            setTimeout(() => {
                if(this.allowGetPriceBounds) {
                    this.fetchProductPriceBounds()
                }
            }, 3000)
        }
    }

    private handleQuantityBoundsValues () {
        if (this.quantityFrom && this.quantityTo) {
            this.allowGetQuantityBounds = false
            setTimeout(() => {
                if(this.allowGetQuantityBounds) {
                    this.fetchProductQuantityBounds()
                }
            }, 3500)
            this.getFilteredProducts()
        } else {
            this.allowGetQuantityBounds = true
            setTimeout(() => {
                if(this.allowGetQuantityBounds) {
                    this.fetchProductQuantityBounds()
                }
            }, 3000)
        }
    }

    @action setFilterDataPriceFrom(priceFrom: number) {
        this.priceFrom = priceFrom
        this.handlePriceBoundsValues()
    }

    @action setFilterDataPriceTo(priceTo: number) {
        this.priceTo = priceTo
        this.handlePriceBoundsValues()
    }

    @action setFilterDataQuantityFrom(quantityFrom: number) {
        this.quantityFrom = quantityFrom
        this.handleQuantityBoundsValues()
    }

    @action setFilterDataQuantityTo(quantityTo: number) {
        this.quantityTo = quantityTo
        this.handleQuantityBoundsValues()
    }

    // установка содержимого списка идентификаторов категорий
    // для фильтра
    @action setFilerDataCategory(id: number, isChecked: boolean) {
        // пытаемся найти из имеющегося списка идентификатор категории,
        // состояние выбора которой сейчас изменилось
        const categoryId =
            this.categories.find(categoryId => categoryId === id)
        // если такого идентифкатора не было в списке,
        // и состояние переключлось в "выбран" -
        // добавляем в список
        if (!categoryId && isChecked) {
            this.categories.push(id)
            // если такой идентифкатор был в списке,
            // и состояние переключлось в "не выбран" -
            // удаляем из списка
        } else if (categoryId && !isChecked) {
            this.categories =
                this.categories.filter(categoryId => categoryId !== id)
        }
        // запрос на бэкенд для получения списка моделей товаров
        // согласно новому состоянию фильтра (набора свойств локального хранилища
        // для фильтрации)
        this.getFilteredProducts()
    }

    @action fetchProducts() {
        commonStore.clearError()
        commonStore.setLoading(true)
        fetch('/eCommerceShop/api/products', {
            method: 'GET'
        }).then((response) => {
            return response.json()
        }).then(responseModel => {
            if (responseModel) {
                if (responseModel.status === 'success') {
                    // полученный объект модели может содержать
                    // свойства, значения которых закодированы из UTF-8 в ASCII,
                    // поэтому производим полное раскодирование:
                    // ts-object конвертируем в json-string (stringify),
                    // декодируем (decodeURIComponent)
                    // json-string конвертируем в  ts-object (parse)
                    this.products =
                        JSON.parse(
                            decodeURIComponent(
                                JSON.stringify(responseModel.data)
                                    .replace(/(%2E)/ig, '%20')
                            )
                        )
                } else if (responseModel.status === 'fail') {
                    commonStore.setError(responseModel.message)
                }
            }
        }).catch((error) => {
            commonStore.setError(error.message)
            throw error
        }).finally(action(() => {
            commonStore.setLoading(false)
        }))
    }

    @action add() {
        commonStore.clearError()
        commonStore.setLoading(true)
        fetch('/eCommerceShop/api/products', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            body: JSON.stringify({
                'title': encodeURIComponent(this.currentProduct.title),
                'description': encodeURIComponent(this.currentProduct.description),
                'price': this.currentProduct.price,
                'quantity': this.currentProduct.quantity,
                'image': this.currentProduct.image,
                'categoryId': this.currentProduct.categoryId
            })
        }).then((response) => {
            return response.status
        }).then(responseStatusCode => {
            if (responseStatusCode) {
                if (responseStatusCode === this.HTTP_STATUS_CREATED) {
                    this.fetchProducts()
                    this.setCurrentProduct(new Product())
                    this.setCurrentProductId(null)
                }
            }
        }).catch((error) => {
            commonStore.setError(error.message)
            throw error
        }).finally(action(() => {
            commonStore.setLoading(false)
        }))
    }

    @action update() {
        commonStore.clearError()
        commonStore.setLoading(true)
        fetch(`/eCommerceShop/api/products/${this.currentProduct.id}`, {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            body: JSON.stringify({
                'title': encodeURIComponent(this.currentProduct.title),
                'description': encodeURIComponent(this.currentProduct.description),
                'price': this.currentProduct.price,
                'quantity': this.currentProduct.quantity,
                'image': this.currentProduct.image,
                'categoryId': this.currentProduct.categoryId
            })
        }).then((response) => {
            return response.status
        }).then(responseStatusCode => {
            if (responseStatusCode) {
                if (responseStatusCode === this.HTTP_STATUS_OK) {
                    this.fetchProducts()
                    this.setProductTitle('')
                    this.setCurrentProduct(new Product())
                    this.setCurrentProductId(null)
                }
            }
        }).catch((error) => {
            commonStore.setError(error.message)
            throw error
        }).finally(action(() => {
            commonStore.setLoading(false)
        }))
    }

    @action deleteProduct() {
        commonStore.clearError()
        commonStore.setLoading(true)
        fetch('/eCommerceShop/api/products/' + this.currentProductId, {
            method: 'DELETE'
        }).then((response) => {
            return response.json()
        }).then(responseModel => {
            if (responseModel) {
                if (responseModel.status === 'success') {
                    this.fetchProducts()
                    this.setCurrentProduct(new Product())
                    this.setCurrentProductId(null)
                } else if (responseModel.status === 'fail') {
                    commonStore.setError(responseModel.message)
                }
            }
        }).catch((error) => {
            commonStore.setError(error.message)
            throw error
        }).finally(action(() => {
            commonStore.setLoading(false)
        }))
    }

    @action getFilteredProducts() {
        commonStore.clearError()
        commonStore.setLoading(true)

        // составление строки запроса к действию контроллера,
        // возвращающему отфильтрованный отсортированный список моделей товаров
        const filteredProductsUrl =
            `api/products/filtered
                        ::orderBy:${this.orderBy}
                        ::sortingDirection:${this.sortingDirection}
                        /?search=
                            price>:${this.priceFrom};
                            price<:${this.priceTo};
                            quantity>:${this.quantityFrom};
                            quantity<:${this.quantityTo}
                            ${(this.categories && this.categories.length > 0) ? ';category:' + JSON.stringify(this.categories) : ''}`

        console.log(filteredProductsUrl)
        // перед запросом на сервер удаляем все пробельные символы из адреса,
        // потому что описанный выше блок кода добавляет их для форматирования
        fetch(filteredProductsUrl.replace(/\s/g, ''), {
            method: 'GET'
        }).then((response) => {
            return response.json()
        }).then(responseModel => {
            if (responseModel) {
                if (responseModel.status === 'success') {
                    this.products =
                        JSON.parse(
                            decodeURIComponent(
                                JSON.stringify(responseModel.data)
                                    .replace(/(%2E)/ig, '%20')
                            )
                        )
                } else if (responseModel.status === 'fail') {
                    commonStore.setError(responseModel.message)
                }
            }
        }).catch((error) => {
            commonStore.setError(error.message)
            throw error
        }).finally(action(() => {
            commonStore.setLoading(false)
        }))
    }

    @action fetchProductPriceBounds() {
        commonStore.clearError()
        commonStore.setLoading(true)
        fetch('/eCommerceShop/api/products/price-bounds', {
            method: 'GET'
        }).then((response) => {
            return response.json()
        }).then(responseModel => {
            if (responseModel) {
                if (responseModel.status === 'success') {
                    this.priceFromBound = responseModel.data.min
                    this.priceToBound = responseModel.data.max
                    if (this.allowGetPriceBounds) {
                        if (!this.priceFrom) {
                            this.priceFrom = this.priceFromBound
                        }
                        if (!this.priceTo) {
                            this.priceTo = this.priceToBound
                        }
                        this.getFilteredProducts()
                    }
                } else if (responseModel.status === 'fail') {
                    commonStore.setError(responseModel.message)
                }
            }
        }).catch((error) => {
            commonStore.setError(error.message)
            throw error
        }).finally(action(() => {
            commonStore.setLoading(false)
        }))
    }

    @action fetchProductQuantityBounds() {
        commonStore.clearError()
        commonStore.setLoading(true)
        fetch('/eCommerceShop/api/products/quantity-bounds', {
            method: 'GET'
        }).then((response) => {
            return response.json()
        }).then(responseModel => {
            if (responseModel) {
                if (responseModel.status === 'success') {
                    this.quantityFromBound = responseModel.data.min
                    this.quantityToBound = responseModel.data.max
                    if (this.allowGetQuantityBounds) {
                        if (!this.quantityFrom) {
                            this.quantityFrom = this.quantityFromBound
                        }
                        if (!this.quantityTo) {
                            this.quantityTo = this.quantityToBound
                        }
                        this.getFilteredProducts()
                    }
                } else if (responseModel.status === 'fail') {
                    commonStore.setError(responseModel.message)
                }
            }
        }).catch((error) => {
            commonStore.setError(error.message)
            throw error
        }).finally(action(() => {
            commonStore.setLoading(false)
        }))
    }

}

export {ProductStore}
export default new ProductStore()