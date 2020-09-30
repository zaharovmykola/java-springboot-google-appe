import React, {Component} from 'react'
import {
    Accordion, AccordionDetails, AccordionSummary, Avatar,
    Button,
    Card,
    CardActionArea, CardActions,
    CardContent,
    CardMedia, Checkbox, Drawer, FormControl, FormControlLabel, FormGroup,
    Grid,
    Icon, InputLabel, MenuItem, Select, Snackbar, TextField,
    Typography, withStyles,
    WithStyles
} from "@material-ui/core";
import Alert from '@material-ui/lab/Alert'
import {inject, observer} from "mobx-react";
import {CommonStore} from "../../stores/CommonStore";
import {ProductStore} from "../../stores/ProductStore";
import {CategoryStore} from "../../stores/CategoryStore";
import {CartStore} from "../../stores/CartStore"
import {UserStore} from '../../stores/UserStore'
import ExpandMoreIcon from '@material-ui/icons/ExpandMore'
import {RouteComponentProps} from 'react-router-dom'

interface MatchParams {
    productId: string
}

interface IProps extends WithStyles<typeof styles>, RouteComponentProps<MatchParams> {
    commonStore: CommonStore,
    productStore: ProductStore,
    categoryStore: CategoryStore,
    cartStore: CartStore,
    userStore: UserStore
}

interface IState {
    sidePanelVisibility: boolean,
    snackBarVisibility: boolean,
    snackBarText: string,
    facebookShareAddress: string
}

const styles = theme =>
    ({
        productCard: {
            maxWidth: 300
        },
        productCardImage: {
            height: 300
        },
        filterButton: {
            position: 'fixed',
            top: 75,
            left: 10,
            zIndex: 999,
            backgroundColor: '#ee6e73'
        },
        drawer: {
            width: 300,
            '& .MuiDrawer-paper': {
                position: 'static'
            }
        },
        heading: {
            fontSize: theme.typography.pxToRem(15),
            fontWeight: theme.typography.fontWeightBold,
            subHeading: {
                fontWeight: theme.typography.fontWeightRegular,
            },
        },
        buttonsort: {
            margin: 1
        },
        facebookButtonImage: {
            width: '100%'
        },
        /*,
        facebookButton: {
            backgroundImage: '/images/shareFacebook.png',
            cursor: 'pointer',
            maxwidth: 60,
            maxheight: 30,
            border: 'none'
        } */
    })

@inject('commonStore', 'productStore', 'categoryStore', 'cartStore', 'userStore')
@observer
class Shopping extends Component<IProps, IState> {

    constructor(props) {
        super(props)
        this.state = {
            sidePanelVisibility: false,
            snackBarVisibility: false,
            snackBarText: '',
            facebookShareAddress: 'https://www.facebook.com/sharer/sharer.php?display=page&u='
        }
    }

    componentDidMount() {
        console.log(this.props.match)
        if (this.props.match && this.props.match.params.productId) {
            console.log(this.props.match.params)
            console.log('prod id = ' + this.props.match.params.productId)
        }
        // сразу после монтирования компонента в виртуальный DOM
        // просим у локальных хранилищ загрузить
        // списки моделей товаров и категорий
        this.props.categoryStore.fetchCategories()
        this.props.productStore.fetchProducts()
        // TODO когда значения границ в локальном хранилище будут получены с сервера -
        // скопировать их в свойства хранилища - priceFrom и priceTo
        this.props.productStore.fetchProductPriceBounds()
        this.props.productStore.fetchProductQuantityBounds()
    }

    toggleDrawer = (open: boolean) => (
        event: React.KeyboardEvent | React.MouseEvent,
    ) => {
        if (
            event.type === 'keydown' &&
            ((event as React.KeyboardEvent).key === 'Tab' ||
                (event as React.KeyboardEvent).key === 'Shift')
        ) {
            return;
        }
        this.setState({sidePanelVisibility: open})
    }

    handleTogglePanelButton = (e) => {
        this.setState({sidePanelVisibility: true})
    }

    // обработчик события "изменение состояния любого из чекбоксов фильтра категорий"
    handleCategoriesFilter = (e, categoryId) => {
        // в хранилище передаем идентификатор категории, значение чекбокса которой
        // изменилось, и само значение (выбран или не выбран)
        this.props.productStore.setFilerDataCategory(categoryId, e.target.checked)
    }

    handlePriceFromChange = e => {
        this.props.productStore.setFilterDataPriceFrom(e.target.value)
    }

    handlePriceToChange = e => {
        this.props.productStore.setFilterDataPriceTo(e.target.value)
    }

    handleQuantityFromChange = e => {
        this.props.productStore.setFilterDataQuantityFrom(e.target.value)
    }

    handleQuantityToChange = e => {
        this.props.productStore.setFilterDataQuantityTo(e.target.value)
    }

    handleAddToCart = (e, productId) => {
        this.props.cartStore.addToCart(productId, () => {
            this.setState({snackBarText: 'One item added to Your cart'})
            this.setState({snackBarVisibility: true})
            setTimeout(() => {
                this.setState({snackBarVisibility: false})
            }, 6000)
        })
    }

    handleFacebookButton = (e, productId) => {
            // var someLink = this.state.facebookShareAddress + productTitle + '' + productDescription
            // this.setState({facebookShareAddress: someLink})
            this.setState({facebookShareAddress: this.state.facebookShareAddress + 'https://github.com/zaharovmykola/springboot-react-shop'})// 'http://localhost:8090/eCommerceShop/shopping/:' + productId})

    }

    handleSnackBarClose = (event?: React.SyntheticEvent, reason?: string) => {
        if (reason === 'clickaway') {
            return;
        }
        this.setState({snackBarVisibility: false})
    }

    handleNewFirstFilter = e => {
        this.props.productStore.setOrderBy('id')
        this.props.productStore.setSortingDirection('DESC')
        this.props.productStore.getFilteredProducts()
    }

    handleOldFirstFilter = e => {
        this.props.productStore.setOrderBy('id')
        this.props.productStore.setSortingDirection('ASC')
        this.props.productStore.getFilteredProducts()
    }

    handleCheepFirstFilter = e => {
        this.props.productStore.setOrderBy('price')
        this.props.productStore.setSortingDirection('ASC')
        this.props.productStore.getFilteredProducts()
    }

    handleExpensiveFirstFilter = e => {
        this.props.productStore.setOrderBy('price')
        this.props.productStore.setSortingDirection('DESC')
        this.props.productStore.getFilteredProducts()
    }

    render() {
        const {loading} = this.props.commonStore
        const {products} = this.props.productStore
        const {categories} = this.props.categoryStore
        const {classes} = this.props
        return <div>
            {/* drawer toggle button */}
            <Button
                variant='outlined'
                disabled={loading}
                className={classes.filterButton}
                onClick={this.handleTogglePanelButton}
            >
                Filter
                <Icon>
                    filter
                </Icon>
            </Button>
            {/* drawer */}
            <Drawer
                open={this.state.sidePanelVisibility}
                onClose={this.toggleDrawer(false)}
                className={classes.drawer}
            >
                <Accordion>
                    <AccordionSummary
                        expandIcon={<ExpandMoreIcon/>}
                        aria-controls="panel2a-content"
                        id="panel2a-header"
                    >
                        <Icon>list</Icon>
                        <Typography className={classes.heading}>
                            Categories
                        </Typography>
                    </AccordionSummary>
                    <AccordionDetails>
                        <FormGroup row>
                            {categories.map(category => {
                                return (
                                    <FormControlLabel
                                        control={
                                            <Checkbox
                                                name={'c' + category.id}
                                                data-category-id={category.id}
                                                checked={!!this.props.productStore.categories.find(categoryId => categoryId === category.id)}
                                                onClick={(e) => {
                                                    this.handleCategoriesFilter(e, category.id)
                                                }}/>
                                        }
                                        label={category.name}
                                        labelPlacement="bottom"/>
                                )
                            })}
                        </FormGroup>
                    </AccordionDetails>
                </Accordion>
                <Accordion>
                    <AccordionSummary
                        expandIcon={<ExpandMoreIcon/>}
                        aria-controls="panel2a-content"
                        id="panel2a-header"
                    >
                        <Icon>filter</Icon>
                        <Typography className={classes.heading}>
                            Filter
                        </Typography>
                    </AccordionSummary>
                    <AccordionDetails>
                        <FormGroup row>
                            <div>
                                <Typography className={classes.subHeading}>
                                    Price
                                </Typography>
                            </div>
                            <div>
                                <TextField
                                    id="priceFrom"
                                    label={'from'}
                                    value={this.props.productStore.priceFrom}
                                    onChange={this.handlePriceFromChange}
                                />
                                <TextField
                                    id="priceTo"
                                    label={'to'}
                                    value={this.props.productStore.priceTo}
                                    onChange={this.handlePriceToChange}
                                />
                            </div>
                        </FormGroup>
                    </AccordionDetails>
                    <AccordionDetails>
                        <FormGroup row>
                            <div>
                                <Typography className={classes.subHeading}>
                                    Quantity
                                </Typography>
                            </div>
                            <div>
                                <TextField
                                    id="quantityFrom"
                                    label={'from'}
                                    value={this.props.productStore.quantityFrom}
                                    onChange={this.handleQuantityFromChange}
                                />
                                <TextField
                                    id="quantityTo"
                                    label={'to'}
                                    value={this.props.productStore.quantityTo}
                                    onChange={this.handleQuantityToChange}
                                />
                            </div>
                        </FormGroup>
                    </AccordionDetails>
                </Accordion>


                <Accordion>
                    <AccordionSummary
                        expandIcon={<ExpandMoreIcon/>}
                        aria-controls="panel2a-content"
                        id="panel2a-header"
                    >
                        <Icon>sort</Icon>
                        <Typography className={classes.heading}>
                            Sort
                        </Typography>
                    </AccordionSummary>
                    <AccordionDetails>

                        <Button
                            className={classes.buttonsort}
                            variant="outlined"
                            onClick={(e) => {
                                this.handleNewFirstFilter(e)
                            }}
                        >
                            New
                        </Button>

                        <Button
                            className={classes.buttonsort}
                            variant="outlined"
                            onClick={(e) => {
                                this.handleOldFirstFilter(e)
                            }}
                        >
                            Old
                        </Button>

                        <Button
                            className={classes.buttonsort}
                            variant="outlined"
                            onClick={(e) => {
                                this.handleCheepFirstFilter(e)
                            }}
                        >
                            Cheep
                        </Button>

                        <Button
                            className={classes.buttonsort}
                            variant="outlined"
                            onClick={(e) => {
                                this.handleExpensiveFirstFilter(e)
                            }}
                        >
                            Costly
                        </Button>

                    </AccordionDetails>
                </Accordion>

            </Drawer>
            <Grid container>
                {products.map(product => {
                    return (
                        <Grid item
                              xs={12}
                              sm={12}
                              md={6}
                              lg={4}
                              xl={3}
                        >
                            <Card className={classes.productCard}>
                                <CardActionArea>
                                    <CardMedia
                                        className={classes.productCardImage}
                                        image={product.image}
                                        title={product.title}
                                    />
                                    <CardContent>
                                        <Typography gutterBottom variant="h5" component="h2">
                                            {product.title} - <strong>${product.price}</strong>
                                        </Typography>
                                        <Typography variant="body2" color="textSecondary" component="p">
                                            {product.description}
                                        </Typography>
                                    </CardContent>
                                </CardActionArea>
                                <CardActions>
                                    <Grid container>
                                        <Grid item
                                            xs={4}>
                                            <Button
                                                className={classes.facebookButton}
                                                onClick={(e) => {
                                                    this.handleFacebookButton(e, product.id)
                                                }}
                                                startIcon={
                                                    <a
                                                        target="_blank"
                                                        href= {this.state.facebookShareAddress}
                                                    >
                                                        <img
                                                            src={'images/shareFacebook.png'}
                                                            className={classes.facebookButtonImage}
                                                        />
                                                    </a>
                                                }
                                            />
                                        </Grid>
                                        <Grid item
                                              xs={8}>
                                            <Button
                                                size="small"
                                                color="primary"
                                                data-product-id={product.id}
                                                onClick={(e) => {
                                                    this.handleAddToCart(e, product.id)
                                                }}
                                                style={{display: this.props.userStore.user ? 'inline' : 'none'}}>
                                                Add to cart
                                            </Button>
                                        </Grid>
                                    </Grid>
                                </CardActions>
                            </Card>
                        </Grid>
                    )

                })}
            </Grid>
            <Snackbar
                open={this.state.snackBarVisibility}
                autoHideDuration={6000} onClose={this.handleSnackBarClose}>
                <Alert onClose={this.handleSnackBarClose} severity="success">
                    {this.state.snackBarText}
                </Alert>
            </Snackbar>
        </div>
    }
}

export default withStyles(styles)(Shopping)