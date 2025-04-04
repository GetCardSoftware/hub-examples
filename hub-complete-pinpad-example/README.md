# **Hub Core PIN Pad Complete Example**

Este é um exemplo completo de integração com o **Hub de Pagamentos**, permitindo configurar o tipo de provedor/dispositivo a ser utilizado para as transações, além de salvar essas configurações em um banco de dados local (SQLite). O exemplo também permite ao usuário informar manualmente os parâmetros de uma transação e um estorno, oferecendo maior flexibilidade para testar diferentes cenários de pagamento.

### Funcionalidade do Exemplo Completo

- **Configuração de Provedor/Dispositivo**: O exemplo permite ao usuário escolher o tipo de provedor de pagamento (como SiTef, Scope, etc.) e configurar os parâmetros do dispositivo (como IP, token, terminal, entre outros).
  
- **Armazenamento em Banco de Dados**: Todas as configurações do provedor e dispositivo são salvas em um banco de dados local usando o **Room** (SQLite).

- **Parâmetros de Transação**: O usuário pode inserir manualmente os parâmetros de uma transação, como valor, tipo de pagamento, entre outros, permitindo simular diferentes tipos de transações.

- **Parâmetros de Estorno**: Após realizar um pagamento, é possível informar os parâmetros necessários para um estorno, utilizando os dados da transação anterior, como o ID da transação e o timestamp.

## Dependências

O projeto depende das bibliotecas **[Hub Interface](https://github.com/GetCardSoftware/hub-interface)**, **[Hub Core](https://github.com/GetCardSoftware/hub-core-pinpad)** e **Room**.

```kotlin
//...
id("com.google.devtools.ksp")
//...
dependencies {
    implementation("com.getcard.hub:hub-interface:1.0.0")
    implementation("com.getcard.hub:core-pinpad:1.0.0-debug")

    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    ksp("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    testImplementation("androidx.room:room-testing:$room_version")
}
```

## Banco de Dados

A persistência das configurações do provedor de pagamento é feita utilizando o **Room**, que gerencia o banco de dados SQLite local. As configurações são armazenadas em duas tabelas principais: **settings** e **sitef_settings**.

### Tabela `settings`

| Campo       | Tipo de Dado | Descrição                          |
|-------------|--------------|------------------------------------|
| device_type | `String`     | Tipo de dispositivo de pagamento. Este campo é a chave primária da tabela. |

### Tabela `sitef_settings`

| Campo       | Tipo de Dado | Descrição                                                                |
|-------------|--------------|--------------------------------------------------------------------------|
| id          | `Int`        | Identificador único da configuração (chave primária). Valor fixo de `1`. |
| server_ip   | `String`     | Endereço IP do servidor do provedor SiTef.                                |
| company     | `String`     | Identificação da empresa.                                                 |
| terminal    | `String`     | Número do terminal utilizado para a comunicação com o servidor.           |
| token       | `String`     | Token de segurança utilizado para autenticação.                           |
| tls         | `Boolean`    | Configuração de segurança TLS para criptografar a comunicação.            |

### Armazenamento e Recuperação de Configurações

O **Room** facilita o gerenciamento do banco de dados, permitindo a persistência das configurações de maneira eficiente. As tabelas são utilizadas para armazenar e recuperar as configurações do provedor de pagamento. Com isso, as configurações podem ser acessadas mesmo após reinicializações do sistema.

## Configuração

Existem duas **activities** dedicadas à configuração das preferências de dispositivo/provedor e seus parâmetros: **ChooseDeviceActivity** e **SitefSettingsActivity**. Ambas as configurações são armazenadas no banco de dados para persistência.

### ChooseDeviceActivity

A **ChooseDeviceActivity** é responsável por permitir a escolha do dispositivo/provedor a ser utilizado. No momento, o único dispositivo disponível é o **SiTef**, mas o sistema foi projetado para permitir futuras expansões com outros provedores.

A interface da activity é configurada utilizando o recurso **`activity_settings.xml`**, que contém os elementos visuais para interação com o usuário.

#### Funcionalidade

- O usuário pode escolher qual dispositivo/provedor será utilizado.
- Após a escolha, a configuração selecionada será salva no banco de dados, utilizando o modelo da tabela **settings**.

### SitefSettingsActivity

A **SitefSettingsActivity** permite configurar os parâmetros específicos do provedor **SiTef**. Esses parâmetros são essenciais para a comunicação com o servidor de pagamento e incluem informações como IP do servidor, token de segurança, terminal, entre outros.

#### Funcionalidade

- O usuário pode definir e ajustar os seguintes parâmetros do **SiTef**:
  - **IP do servidor**
  - **Token de segurança**
  - **Identificação da empresa**
  - **Número do terminal**
  - **Configuração de TLS**
  
Essas configurações serão salvas no banco de dados, utilizando o modelo da tabela **sitef_settings**, garantindo que as configurações sejam persistidas e acessíveis a qualquer momento.

### Persistência das Configurações

As configurações definidas nas duas activities são salvas no banco de dados utilizando o **Room**. O **ChooseDeviceActivity** salva o tipo de dispositivo (como **SiTef**) na tabela **settings**, enquanto a **SitefSettingsActivity** salva os parâmetros de configuração do SiTef na tabela **sitef_settings**.

## Pagamento e Estorno

As operações de **pagamento** e **estorno** são processadas utilizando os parâmetros fornecidos na classe `TransactionParams`. A diferença entre as operações está nos dados informados nesse objeto.

### InitTransactionActivity

A **InitTransactionActivity** é responsável por coletar os dados necessários para a realização de uma transação de pagamento. O usuário informa:

- **Valor da transação**  
- **Modalidade de pagamento** (débito ou crédito)  
- **Tipo de parcelamento** (à vista ou parcelado)  
- **Número de parcelas** (caso o pagamento seja parcelado)  

Após a coleta das informações, a activity cria um objeto da classe `TransactionParams` com os dados fornecidos e inicia a **PaymentActivity** para processar a transação.

```kotlin
class InitTransactionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInitTransactionBinding
    private var choosedInstallmentType: InstallmentType = InstallmentType.ONE_TIME
    private var choosedPaymentType: PaymentType = PaymentType.DEBIT

    override fun onCreate(savedInstanceState: Bundle?) {
        //...

        //Configuração do menu para escolha do tipo de pagamento (choosedPaymentType)

        //Configuração do menu para escolha do tipo de parcelamento (choosedInstallmentType)

        binding.startTransactionButton.setOnClickListener {
            val value = binding.amountTextField.string().formatValue()
            if (value.isEmpty() || value.toDouble() == 0.0) {
                binding.amountTextField.error = "Digite o valor da transação"
                Toast.makeText(this, "Digite o valor da transação", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val installmentNumber = binding.installmentNumberField.string()
            if (installmentNumber.isEmpty() || installmentNumber.toInt() == 0) {
                binding.installmentNumberField.error = "Digite o número de parcelas"
                Toast.makeText(this, "Digite o número de parcelas", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(this, PaymentActivity::class.java)
            intent.putExtra(
                "TRANSACTION_PARAMS",
                TransactionParams(
                    value.toBigDecimal(),
                    choosedPaymentType,
                    choosedInstallmentType,
                    installmentNumber.toInt()
                )
            )
            startActivity(intent)
        }   
    }       
}
```

### InitRefundActivity

A **InitRefundActivity** tem a mesma função da **InitTransactionActivity**, mas voltada para a coleta dos dados de um estorno. O usuário informa:

- **ID da transação**  
- **Timestamp da transação**  
- **Valor a ser estornado**  
- **Modalidade do pagamento original** (débito ou crédito)  

Os dados coletados são utilizados para construir um objeto da classe `TransactionParams`, indicando que se trata de um estorno, e a **RefundActivity** é iniciada para processar a operação.

```kotlin
class InitRefundActivity : AppCompatActivity() {

    //...
    private var choosedPaymentType: PaymentType = PaymentType.DEBIT

    //...

    override fun onCreate(savedInstanceState: Bundle?) {

        private lateinit var binding: ActivityInitRefundBinding
        private var choosedPaymentType: PaymentType = PaymentType.DEBIT

        // Configuração do menu para escolha do tipo de pagamento (choosedPaymentType)

        binding.amountTextField.setCurrency()

        binding.startRefundButton.setOnClickListener {
            val nsuHost = binding.nsuHostText.string()
            val transactionTimestamp = binding.transactionTimestampText.string()
            val amount = binding.amountTextField.string().formatValue()

            if (transactionId.isEmpty() || transactionTimestamp.isEmpty() || amount.isEmpty() || amount.toDouble() == 0.0) {
                Toast.makeText(
                    this,
                    "Por favor, preencha todos os campos",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val refundIntent = Intent(this, RefundActivity::class.java)
            refundIntent.putExtra(
                "REFUND_PARAMS", TransactionParams(
                    refund = true,
                    transactionId = transactionId,
                    transactionTimestamp = transactionTimestamp.toLong(),
                    amount = amount.toBigDecimal(),
                    paymentType = choosedPaymentType,
                )
            )
            startActivity(refundIntent)
        }

    }
}
```

### Processamento da Transação

A **PaymentActivity** recebe um objeto da classe `TransactionParams` contendo os dados da operação e realiza a transação. Para estornos, a **RefundActivity** é responsável pelo processamento. Após a conclusão, um objeto da classe `TransactionResponse` é gerado contendo as informações do resultado.

#### Estrutura do `TransactionParams`

| Campo                | Tipo           | Descrição |
|----------------------|---------------|-----------|
| amount               | `BigDecimal`   | Valor da transação. |
| paymentType          | `PaymentType`  | Modalidade do pagamento (débito ou crédito). |
| installmentType      | `InstallmentType` | Tipo de parcelamento (à vista ou parcelado). |
| installmentNumber    | `Int`          | Número de parcelas, se aplicável. |
| nsuHost              | `String?`      | ID da transação (necessário apenas para estorno). |
| refund               | `Boolean`      | Indica se a operação é um estorno. |
| transactionTimestamp | `Long?`      | Timestamp da transação original (necessário para estorno). |

#### Estrutura do `TransactionResponse`

| Campo                | Tipo           | Descrição |
|----------------------|---------------|-----------|
| status               | `OperationStatus` | Código de status da operação. |
| message              | `String`       | Mensagem de retorno da transação. |
| transactionTimestamp | `Long`         | Timestamp da transação realizada. |
| nsuHost              | `String?`      | ID da transação (se bem-sucedida). |
| customerReceipt      | `String?`      | Comprovante do cliente (se disponível). |
| establishmentReceipt | `String?`      | Comprovante do estabelecimento (se disponível). |

### Tipos Utilizados

#### `PaymentType`
| Valor   | Descrição        |
|---------|-----------------|
| `DEBIT`  | Pagamento no débito. |
| `CREDIT` | Pagamento no crédito. |

#### `InstallmentType`
| Valor         | Descrição               |
|--------------|------------------------|
| `ONE_TIME`   | Pagamento à vista.      |
| `INSTALLMENTS` | Pagamento parcelado. |

#### `OperationStatus`
| Valor      | Descrição                          |
|-----------|----------------------------------|
| `SUCCESS`  | Transação aprovada.             |
| `DECLINED` | Transação recusada.             |
| `FAILED`   | Falha na transação.             |
| `CANCELLED` | Transação cancelada.           |
| `UNKNOWN`  | Status desconhecido da transação. |

### Exibição do Resultado

Após a conclusão bem-sucedida da transação, a **PaymentActivity** exibe uma **dialog** ao usuário com as seguintes informações retiradas do objeto `TransactionResponse`:

- **ID da transação**
- **Timestamp da transação**
- **Comprovante da transação** (quando disponível)

Isso permite que o usuário visualize e confirme o resultado da operação.

## Processamento de Pagamentos e Estornos

O processamento de pagamentos e estornos é realizado pelas activities **PaymentActivity** e **RefundActivity**, respectivamente. Ambas utilizam um objeto `PaymentCentral` para executar a operação correspondente.

```kotlin
class PaymentActivity : AppCompatActivity() {
//class RefundActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val paymentCentral = Utils.getPaymentCentral(this)

        //...

        val paymentParams = intent.getParcelableExtra<TransactionParams>("TRANSACTION_PARAMS")
        //val refundParams = intent.getParcelableExtra<TransactionParams>("REFUND_PARAMS")

        //...

        lifecycleScope.launch {
            val paymentResult = try {
                paymentCentral.pay(this@PaymentActivity, paymentParams)
            // val refundResult = try {
            //     paymentCentral.pay(this@RefundActivity, refundParams)
            } catch (e: Exception) {
                //...
            }

            //...

            if (paymentResult.status == OperationStatus.SUCCESS) {
            // if (refundResult.status == OperationStatus.SUCCESS) {
                //...

                val resultIntent = intent
                resultIntent.putExtra("TRANSACTION_RESULT", refundResult)
                setResult(RESULT_OK, resultIntent)
            } else {
                finish()
            }
        }
    }
}

```

> O método `pay()` é **suspenso**, ou seja, **ele aguardará a finalização da transação** e retornará
> um `TransactionResponse`.
