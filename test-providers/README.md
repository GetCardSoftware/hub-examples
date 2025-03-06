# **Hub Core Simple PIN Pad Example**

Este é um exemplo de como integrar as bibliotecas do **Hub de Pagamentos** de maneira simples à um aplicativo.  
Ele é composto por apenas duas _activities_, e exemplifica como realizar um pagamento e um estorno de pagamento.

## Dependências

O projeto depende das bibliotecas **[Hub Interface](https://github.com/GetCardSoftware/hub-interface)** e **[Hub Core](https://github.com/GetCardSoftware/hub-core-pinpad)**.

```kotlin
dependencies {
    implementation("com.getcard.hub:hub-interface:1.0.0")
    implementation("com.getcard.hub:core-pinpad:1.0.0-debug")
}
```

# MainActivity

A `MainActivity` é a tela principal do aplicativo e contém dois botões:

1. **Botão de Transação**: Inicia a `PaymentActivity` para realizar um pagamento.
2. **Botão de Estorno**: Inicia a `PaymentActivity`, aguarda o retorno da transação e, se bem-sucedida, chama novamente a `PaymentActivity` para realizar o estorno.

## Configuração dos Botões

```kotlin
binding.transactionButton.setOnClickListener {
    startActivity(paymentIntent)
}

binding.refundButton.setOnClickListener {
    launcher.launch(paymentIntent)
}
```

O botão de transação inicia diretamente a `PaymentActivity`. Já o botão de estorno usa um `ActivityResultLauncher` para capturar a resposta da transação antes de iniciar o estorno.

## Configuração da `Intent` de Pagamento

```kotlin
val paymentIntent = Intent(this, PaymentActivity::class.java)
paymentIntent.putExtra("TRANSACTION_PARAMS",TransactionParams(
    amount = BigDecimal("2000"),
    paymentType = PaymentType.CREDIT
))
```

Essa `Intent` define os parâmetros da transação, incluindo o valor e o tipo de pagamento, e será usada para chamar a `PaymentActivity`.  
Para mais detalhes sobre os parâmetros de transação veja na **[Hub Interface](https://github.com/GetCardSoftware/hub-interface?tab=readme-ov-file#transactionparams)**.

## Capturando o Resultado da Transação

```kotlin
val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
    val response = result.data?.getParcelableExtra<TransactionResponse>("TRANSACTION_RESULT")
    if (response != null) {
        Log.d("PaymentActivity", "Response: $response")
        val intent = Intent(this, PaymentActivity::class.java)
        intent.putExtra("TRANSACTION_PARAMS", TransactionParams(
            amount = BigDecimal("2000"),
            transactionId = response.transactionId,
            transactionTimestamp = response.transactionTimestamp,
            refund = true,
            paymentType = PaymentType.CREDIT
        ))
        startActivity(intent)
    }
}
```

Caso a transação seja bem-sucedida, a resposta é extraída do _extra_ de nome **TRANSACTION_RESULT**, e os dados de ID e _timestamp_ da transação são usados como parâmetros da `TransactionParams` para criar uma nova `Intent` e realizar o estorno da transação original.  
Para mais detalhes sobre a resposta da transação `TransactionResponse` veja na **[Hub Interface](https://github.com/GetCardSoftware/hub-interface?tab=readme-ov-file#transactionresponse)**.  
  
# PaymentActivity

A `PaymentActivity` é responsável por efetivar as transações financeiras.

## Implementação

### Configuração do Provedor de Pagamento

```kotlin
val providerConfig = PaymentProviderConfig.builder()
    .setIp("192.168.1.227")
    .setToken("2823-0548-7763-1206")
    .setCompany("00000000")
    .setTerminal("50201136")
    .build()
```

Aqui, os detalhes de configuração do provedor de pagamento (SiTef, Scope, etc...) são definidos.  Cada provedor terá seus próprios parâmetros de configuração, portanto nem sempre serão necessários informar todos os parâmetros.  
Para mais detalhes veja a documentação na **[Hub Interface](https://github.com/GetCardSoftware/hub-interface?tab=readme-ov-file#configura%C3%A7%C3%A3o)**.

### Realizar o pagamento

A classe `PaymentCentral`, e a responsável pelo processamento das transações. O provedor que será utilizar e suas configurações são informados no momento em que a classe é instanciada.

```kotlin
val paymentCentral = PaymentCentral(DeviceType.SITEF, providerConfig)
```

É **ESSENCIAL** que o método `pay()` seja chamado dentro de uma **corotina** e do método `onCreate()` da activity `PaymentCentral`.

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    //...
    lifecycleScope.launch {
        val paymentResult = try {
            paymentCentral.pay(this@PaymentActivity, paymentParams)
        } catch (e: Exception) {
            //...
        }
        /...
    }
    //...
}
```
